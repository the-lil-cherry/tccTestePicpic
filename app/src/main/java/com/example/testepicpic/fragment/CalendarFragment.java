package com.example.testepicpic.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.print.PrintAttributes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.testepicpic.R;
import com.example.testepicpic.activity.DiaSelecionadoActivity;
import com.example.testepicpic.config.ConfigFirebase;
import com.example.testepicpic.helper.Base64Custom;
import com.example.testepicpic.model.Alimentacao;
import com.example.testepicpic.model.BemEstar;
import com.example.testepicpic.model.Exercicio;
import com.example.testepicpic.model.Glicemia;
import com.example.testepicpic.model.Insulina;
import com.example.testepicpic.model.Usuario;
import com.google.android.gms.common.internal.service.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment {

    private DatabaseReference ref;
    private String currentId;

    public static final String PREFS_NAME = "shareData";

    private String miau = "";

    private List<EventDay> eventDays = new ArrayList<>();
    private Calendar calendar;
    private CalendarView calendarView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_calendar, container, false);

        recuperarUsuario();

        ref = ConfigFirebase.getFirebase();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        recuperarUsuario();

        ref = ConfigFirebase.getFirebase();

        calendarView = getView().findViewById(R.id.calendarView);

        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        int anoMin = Calendar.getInstance().get(Calendar.YEAR);
        int mesMin = (Calendar.getInstance().get(Calendar.MONTH)-2);
        int diaMin = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        int anoMax = Calendar.getInstance().get(Calendar.YEAR);
        int mesMax = (Calendar.getInstance().get(Calendar.MONTH)+1);
        int diaMax = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        min.set(anoMin, mesMin, diaMin);
        max.set(anoMax, mesMax, diaMax);

        calendarView.setMinimumDate(min);
        calendarView.setMaximumDate(max);

        try {

            //add glicemia
            DatabaseReference referenceGli = ref.child("inserção")
                    .child(currentId)
                    .child("glicemia");

            referenceGli.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for ( DataSnapshot dataSnapshot : snapshot.getChildren() ) {

                        DatabaseReference reference1 = ref.child("inserção")
                                .child(currentId)
                                .child("glicemia")
                                .child(dataSnapshot.getKey());

                        reference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                calendar = Calendar.getInstance();

                                int[] dias  =  new int[(int) snapshot.getChildrenCount()];
                                int[] meses =  new int[(int) snapshot.getChildrenCount()];
                                int[] anos  =  new int[(int) snapshot.getChildrenCount()];

                                for( DataSnapshot dataSnapshot1 : snapshot.getChildren() ) {

                                    Glicemia glicemia = dataSnapshot1.getValue(Glicemia.class);

                                    for (int i = 0; i < snapshot.getChildrenCount(); i++) {
                                        dias[i] = glicemia.getDia();
                                        meses[i] = (glicemia.getMes() - 1);
                                        anos[i] = glicemia.getAno();
                                        calendar.set(anos[i],meses[i],dias[i]);
                                    }

                                    eventDays.add(new EventDay(calendar, R.drawable.ic_glucose_meter));
                                    calendarView.setEvents(eventDays);

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //add insulina
            DatabaseReference referenceInsu = ref.child("inserção")
                    .child(currentId)
                    .child("insulina");

            referenceInsu.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for ( DataSnapshot dataSnapshot : snapshot.getChildren() ) {

                        DatabaseReference reference1 = ref.child("inserção")
                                .child(currentId)
                                .child("insulina")
                                .child(dataSnapshot.getKey());

                        reference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                calendar = Calendar.getInstance();

                                int[] dias  =  new int[(int) snapshot.getChildrenCount()];
                                int[] meses =  new int[(int) snapshot.getChildrenCount()];
                                int[] anos  =  new int[(int) snapshot.getChildrenCount()];

                                for( DataSnapshot dataSnapshot1 : snapshot.getChildren() ) {

                                    Insulina insulina = dataSnapshot1.getValue(Insulina.class);

                                    for (int i = 0; i < snapshot.getChildrenCount(); i++) {
                                        dias[i] = insulina.getDia();
                                        meses[i] = (insulina.getMes() - 1);
                                        anos[i] = insulina.getAno();
                                        calendar.set(anos[i],meses[i],dias[i]);
                                    }

                                    eventDays.add(new EventDay(calendar, R.drawable.ic_insulin));
                                    calendarView.setEvents(eventDays);

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //add exercicio
            DatabaseReference referenceEx = ref.child("inserção")
                    .child(currentId)
                    .child("exercicio");

            referenceEx.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for ( DataSnapshot dataSnapshot : snapshot.getChildren() ) {

                        DatabaseReference reference1 = ref.child("inserção")
                                .child(currentId)
                                .child("exercicio")
                                .child(dataSnapshot.getKey());

                        reference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                calendar = Calendar.getInstance();

                                int[] dias  =  new int[(int) snapshot.getChildrenCount()];
                                int[] meses =  new int[(int) snapshot.getChildrenCount()];
                                int[] anos  =  new int[(int) snapshot.getChildrenCount()];

                                for( DataSnapshot dataSnapshot1 : snapshot.getChildren() ) {

                                    Exercicio exercicio = dataSnapshot1.getValue(Exercicio.class);

                                    for (int i = 0; i < snapshot.getChildrenCount(); i++) {
                                        dias[i] = exercicio.getDia();
                                        meses[i] = (exercicio.getMes() - 1);
                                        anos[i] = exercicio.getAno();
                                        calendar.set(anos[i],meses[i],dias[i]);
                                    }

                                    eventDays.add(new EventDay(calendar, R.drawable.academia));
                                    calendarView.setEvents(eventDays);

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //add alimentacao
            DatabaseReference referenceAli = ref.child("inserção")
                    .child(currentId)
                    .child("alimentação");

            referenceAli.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for ( DataSnapshot dataSnapshot : snapshot.getChildren() ) {

                        DatabaseReference reference1 = ref.child("inserção")
                                .child(currentId)
                                .child("alimentação")
                                .child(dataSnapshot.getKey());

                        reference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                calendar = Calendar.getInstance();

                                int[] dias  =  new int[(int) snapshot.getChildrenCount()];
                                int[] meses =  new int[(int) snapshot.getChildrenCount()];
                                int[] anos  =  new int[(int) snapshot.getChildrenCount()];

                                for( DataSnapshot dataSnapshot1 : snapshot.getChildren() ) {

                                    Alimentacao alimentacao = dataSnapshot1.getValue(Alimentacao.class);

                                    for (int i = 0; i < snapshot.getChildrenCount(); i++) {
                                        dias[i] = alimentacao.getDia();
                                        meses[i] = (alimentacao.getMes() - 1);
                                        anos[i] = alimentacao.getAno();
                                        calendar.set(anos[i],meses[i],dias[i]);
                                    }

                                    eventDays.add(new EventDay(calendar, R.drawable.ic_restaurant));
                                    calendarView.setEvents(eventDays);

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //add bem-estar
            DatabaseReference referenceBem = ref.child("inserção")
                    .child(currentId)
                    .child("bem-estar");

            referenceBem.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for ( DataSnapshot dataSnapshot : snapshot.getChildren() ) {

                        DatabaseReference reference1 = ref.child("inserção")
                                .child(currentId)
                                .child("bem-estar")
                                .child(dataSnapshot.getKey())
                                .child("geral");

                        reference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                calendar = Calendar.getInstance();

                                int[] dias  =  new int[(int) snapshot.getChildrenCount()];
                                int[] meses =  new int[(int) snapshot.getChildrenCount()];
                                int[] anos  =  new int[(int) snapshot.getChildrenCount()];

                                BemEstar bemEstar = snapshot.getValue(BemEstar.class);

                                for (int i = 0; i < snapshot.getChildrenCount(); i++) {
                                    dias[i] = bemEstar.getDia();
                                    meses[i] = (bemEstar.getMes() - 1);
                                    anos[i] = bemEstar.getAno();
                                    calendar.set(anos[i],meses[i],dias[i]);
                                }

                                eventDays.add(new EventDay(calendar, R.drawable.ic_emoticons));

                                calendarView.setEvents(eventDays);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } catch (Exception e) {

        }

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(@NotNull EventDay eventDay) {

                Calendar date = eventDay.getCalendar();

                int today = date.get(Calendar.DAY_OF_MONTH);
                int month = (date.get(Calendar.MONTH)+1);
                int year  = date.get(Calendar.YEAR);

                SharedPreferences.Editor preferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
                preferences.putInt("today", today);
                preferences.putInt("month", month);
                preferences.putInt("year", year);
                preferences.apply();

                startActivity(new Intent(getActivity(),DiaSelecionadoActivity.class));

            }
        });

    }

    public void recuperarUsuario() {

        FirebaseAuth auth = ConfigFirebase.getFirebaseAutenticacao();

        if(auth.getCurrentUser() != null) {

            String email = auth.getCurrentUser().getEmail();
            assert email != null;
            currentId = Base64Custom.codificarBase64(email);
        }

    }

}