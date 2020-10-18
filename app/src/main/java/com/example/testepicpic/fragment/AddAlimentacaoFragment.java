package com.example.testepicpic.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.testepicpic.R;
import com.example.testepicpic.config.ConfigFirebase;
import com.example.testepicpic.helper.Base64Custom;
import com.example.testepicpic.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddAlimentacaoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddAlimentacaoFragment extends Fragment {

    private Button btnAddAliCafe, btnAddAliAlmoco, btnAddAliJanta, btnAddAliLanches, btnCancelar, btnAliDia, btnSalvar;
    private ConstraintLayout clAddAli;
    private CheckBox cVegetais, cFrutas, cLegumes, cGraos, cIntegrais, cBatata, cOvo, cLaticinios, cNozes, cPeixe, cCarne, cDoce, cAperitivos, cLanches, cAlcool, cAdocante, cSuplementos, cRefriDiet, cRefri;
    private EditText edtDescricaoAli;

    private boolean[] pComidasCafe = new boolean[19];
    private boolean[] pComidasAlmoco = new boolean[19];
    private boolean[] pComidasJanta = new boolean[19];
    private boolean[] pComidasLanches = new boolean[19];

    private FirebaseAuth auth;
    private DatabaseReference ref;

    private String strPComidasCafe;

    private int pDay, pMonth, pYear;

    private int indice;

    private String currentId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddAlimentacaoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddAlimentacao.
     */
    // TODO: Rename and change types and number of parameters
    public static AddAlimentacaoFragment newInstance(String param1, String param2) {
        AddAlimentacaoFragment fragment = new AddAlimentacaoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_alimentacao, container, false);

        btnAliDia = view.findViewById(R.id.btnAliDia);

        btnAddAliAlmoco = view.findViewById(R.id.btnAddAliAlmoco);
        btnAddAliJanta = view.findViewById(R.id.btnAddAliJanta);
        btnAddAliCafe = view.findViewById(R.id.btnAddAliCafe);
        btnAddAliLanches = view.findViewById(R.id.btnAddAliLanches);

        btnSalvar = view.findViewById(R.id.btnSalvar);
        btnCancelar = view.findViewById(R.id.btnCancelar);

        cVegetais = view.findViewById(R.id.checkBox);
        cFrutas = view.findViewById(R.id.checkBox3);
        cLegumes = view.findViewById(R.id.checkBox4);
        cGraos = view.findViewById(R.id.checkBox6);
        cIntegrais = view.findViewById(R.id.checkBox7);
        cBatata = view.findViewById(R.id.checkBox2);
        cOvo = view.findViewById(R.id.checkBox8);
        cLaticinios = view.findViewById(R.id.checkBox9);
        cNozes = view.findViewById(R.id.checkBox10);
        cPeixe = view.findViewById(R.id.checkBox11);
        cCarne = view.findViewById(R.id.checkBox14);
        cDoce = view.findViewById(R.id.checkBox15);
        cAperitivos = view.findViewById(R.id.checkBox19);
        cLanches = view.findViewById(R.id.checkBox16);
        cAlcool = view.findViewById(R.id.checkBox18);
        cAdocante = view.findViewById(R.id.checkbox20);
        cSuplementos = view.findViewById(R.id.checkbox21);
        cRefriDiet = view.findViewById(R.id.checkbox22);
        cRefri = view.findViewById(R.id.checkbox23);

        edtDescricaoAli = view.findViewById(R.id.edtDescicaoAli);

        clAddAli = view.findViewById(R.id.clAddAli);

        final CheckBox[] comidas = {cVegetais, cFrutas, cLegumes, cGraos, cIntegrais, cBatata, cOvo, cLaticinios, cNozes, cPeixe, cCarne, cDoce, cAperitivos, cLanches, cAlcool, cAdocante, cSuplementos, cRefriDiet, cRefri};

        final String[] listaComidas = {"Vegetais", "Frutas", "Legumes", "Grãos", "Integrais",
                "Batata", "Ovo", "Laticínios", "Nozes", "Peixe", "Carne", "Doce", "Aperitivos",
                "Lanches", "Alcool", "Adoçante", "Suplementos", "Refri Diet", "Refri"};

        btnAliDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        btnAliDia.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                        pDay = dayOfMonth;
                        pMonth = (month+1);
                        pYear = year;
                    }
                }, year, month, day);

                datePickerDialog.show();

            }
        });

        btnAddAliCafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                indice = 0;

                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.to_down);

                clAddAli.startAnimation(animation);

                clAddAli.setVisibility(View.VISIBLE);

            }
        });

        btnAddAliAlmoco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                indice = 1;

                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.to_down);

                clAddAli.startAnimation(animation);

                clAddAli.setVisibility(View.VISIBLE);

            }
        });

        btnAddAliJanta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                indice = 2;

                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.to_down);

                clAddAli.startAnimation(animation);

                clAddAli.setVisibility(View.VISIBLE);


            }
        });

        btnAddAliLanches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                indice = 3;

                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.to_down);

                clAddAli.startAnimation(animation);

                clAddAli.setVisibility(View.VISIBLE);

            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recuperarUsurario();

                strPComidasCafe = edtDescricaoAli.getText().toString();

                ref =ConfigFirebase.getFirebase();

                if(btnAliDia.getText().toString().equals("Hoje")) {
                    pYear = Calendar.getInstance().get(Calendar.YEAR);
                    pMonth = (Calendar.getInstance().get(Calendar.MONTH)+1);
                    pDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                }

                switch(indice) {
                    case 0:
                        if(!strPComidasCafe.equals("")) {

                            AlertDialog.Builder dialogC = new AlertDialog.Builder(getActivity());

                            dialogC.setTitle("Deseja mesmo salvar?");

                            dialogC.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    for(int i = 0; i < comidas.length; i++) {
                                        if(comidas[i].isChecked())
                                            pComidasCafe[i] = true;
                                    }

                                    for(int i = 0; i < pComidasCafe.length; i++) {
                                        ref.child("users")
                                                .child(currentId)
                                                .child("inserção")
                                                .child("alimentação")
                                                .child("café")
                                                .child(String.valueOf(pYear))
                                                .child(String.valueOf(pMonth))
                                                .child(String.valueOf(pDay))
                                                .child(listaComidas[i])
                                                .setValue(pComidasCafe[i]);
                                    }

                                    ref.child("users")
                                            .child(currentId)
                                            .child("inserção")
                                            .child("alimentação")
                                            .child("café")
                                            .child(String.valueOf(pYear))
                                            .child(String.valueOf(pMonth))
                                            .child(String.valueOf(pDay))
                                            .child("descrição")
                                            .setValue(strPComidasCafe);

                                    Toast.makeText(getActivity(), "Já salvamos :)", Toast.LENGTH_SHORT).show();

                                    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.from_top);

                                    clAddAli.startAnimation(animation);

                                    clAddAli.setVisibility(View.GONE);

                                    for(CheckBox comida : comidas) {
                                        if(comida.isChecked())
                                            comida.setChecked(false);
                                    }

                                    if(edtDescricaoAli.getText() != null)
                                        edtDescricaoAli.setText(null);
                                }
                            });

                            dialogC.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Toast.makeText(getActivity(),"Ok, cancelamos", Toast.LENGTH_SHORT).show();

                                }
                            });

                            dialogC.create();
                            dialogC.show();

                        } else {
                            Toast.makeText(getActivity(), "Por favor, preencha a descrição da refeição", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case 1:
                        if(!strPComidasCafe.equals("")) {
                            AlertDialog.Builder dialogC = new AlertDialog.Builder(getActivity());

                            dialogC.setTitle("Deseja mesmo salvar?");

                            dialogC.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for(int i = 0; i < comidas.length; i++) {
                                if(comidas[i].isChecked())
                                    pComidasJanta[i] = true;
                            }

                            for(int i = 0; i < pComidasAlmoco.length; i++) {
                                ref.child("users")
                                        .child(currentId)
                                        .child("inserção")
                                        .child("alimentação")
                                        .child("café")
                                        .child(String.valueOf(pYear))
                                        .child(String.valueOf(pMonth))
                                        .child(String.valueOf(pDay))
                                        .child(listaComidas[i])
                                        .setValue(pComidasAlmoco[i]);
                            }

                            ref.child("users")
                                    .child(currentId)
                                    .child("inserção")
                                    .child("alimentação")
                                    .child("Almoço")
                                    .child(String.valueOf(pYear))
                                    .child(String.valueOf(pMonth))
                                    .child(String.valueOf(pDay))
                                    .child("descrição")
                                    .setValue(strPComidasCafe);
                                }
                                    });


                                dialogC.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Toast.makeText(getActivity(),"Ok, cancelamos", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            dialogC.create();
                            dialogC.show();

                        } else {
                            Toast.makeText(getActivity(), "Por favor, preencha a descrição da refeição", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case 2:
                        if(!strPComidasCafe.equals("")) {

                            AlertDialog.Builder dialogC = new AlertDialog.Builder(getActivity());

                            dialogC.setTitle("Deseja mesmo salvar?");

                            dialogC.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                     for(int i = 0; i < comidas.length; i++) {
                                if(comidas[i].isChecked())
                                    pComidasJanta[i] = true;
                            }

                            for(int i = 0; i < pComidasJanta.length; i++) {
                                ref.child("users")
                                        .child(currentId)
                                        .child("inserção")
                                        .child("alimentação")
                                        .child("café")
                                        .child(String.valueOf(pYear))
                                        .child(String.valueOf(pMonth))
                                        .child(String.valueOf(pDay))
                                        .child(listaComidas[i])
                                        .setValue(pComidasJanta[i]);
                            }

                            ref.child("users")
                                    .child(currentId)
                                    .child("inserção")
                                    .child("alimentação")
                                    .child("Almoço")
                                    .child(String.valueOf(pYear))
                                    .child(String.valueOf(pMonth))
                                    .child(String.valueOf(pDay))
                                    .child("descrição")
                                    .setValue(strPComidasCafe);
                                }
                            });


                            dialogC.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Toast.makeText(getActivity(),"Ok, cancelamos", Toast.LENGTH_SHORT).show();

                                }
                            });

                            dialogC.create();
                            dialogC.show();


                        } else {
                            Toast.makeText(getActivity(), "Por favor, preencha a descrição da refeição", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case 3:
                        if(strPComidasCafe != null) {
                            AlertDialog.Builder dialogC = new AlertDialog.Builder(getActivity());

                            dialogC.setTitle("Deseja mesmo salvar?");

                            dialogC.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for(int i = 0; i < comidas.length; i++) {
                                        if(comidas[i].isChecked())
                                            pComidasLanches[i] = true;
                                    }

                                    for(int i = 0; i < pComidasLanches.length; i++) {
                                        ref.child("users")
                                                .child(currentId)
                                                .child("inserção")
                                                .child("alimentação")
                                                .child("café")
                                                .child(String.valueOf(pYear))
                                                .child(String.valueOf(pMonth))
                                                .child(String.valueOf(pDay))
                                                .child(listaComidas[i])
                                                .setValue(pComidasLanches[i]);
                                    }

                                    ref.child("users")
                                            .child(currentId)
                                            .child("inserção")
                                            .child("alimentação")
                                            .child("Almoço")
                                            .child(String.valueOf(pYear))
                                            .child(String.valueOf(pMonth))
                                            .child(String.valueOf(pDay))
                                            .child("descrição")
                                            .setValue(strPComidasCafe);
                                }
                            });


                            dialogC.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Toast.makeText(getActivity(),"Ok, cancelamos", Toast.LENGTH_SHORT).show();

                                }
                            });

                            dialogC.create();
                            dialogC.show();
                        } else {
                            Toast.makeText(getActivity(), "Por favor, preencha a descrição da refeição", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.from_top);

                clAddAli.startAnimation(animation);

                clAddAli.setVisibility(View.GONE);

                for(CheckBox comida : comidas) {
                    if(comida.isChecked())
                        comida.setChecked(false);
                }

                if(edtDescricaoAli.getText() != null)
                    edtDescricaoAli.setText(null);

            }
        });

        return view;
    }

    public void recuperarUsurario() {
        auth = ConfigFirebase.getFirebaseAutenticacao();

        String email = auth.getCurrentUser().getEmail();
        assert email != null;
        currentId = Base64Custom.codificarBase64(email);
    }

}