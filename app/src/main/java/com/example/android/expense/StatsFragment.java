package com.example.android.expense;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fr    agment.
 */
public class StatsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private LineChart lineChart;
    private Database db;
    private HorizontalBarChart horBarChart;

    public StatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setIDs();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayContent();
    }

    private void setIDs() {
        lineChart = getActivity().findViewById(R.id.line_chart);
        db = new Database(this.getContext());
    }

    private void displayContent() {

        displayCubicLineChart();
    }

    private void displayCubicLineChart() {
        ArrayList<String> categories = db.getWeeksExpenseCategories();

        ArrayList amounts= new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            long amount = db.getWeeksExpenseAmountByCategory(categories.get(i));
            amounts.add(new BarEntry(amount, i));
        }

        LineDataSet lineDataSet = new LineDataSet(amounts, "Amount");
        lineChart.setTouchEnabled(false);
        lineChart.setDescription(" ");
        lineDataSet.setDrawCubic(true);
        lineDataSet.setDrawFilled(true);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setTextColor(Color.BLACK);
        lineChart.getXAxis().setTextSize(10f);
        lineChart.getXAxis().setDrawAxisLine(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawAxisLine(false);
        lineChart.getAxisRight().setDrawLabels(false);
        LineData data = new LineData(categories, lineDataSet);
        lineDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        lineChart.setData(data);
        lineChart.animateXY(1000, 1000);
    }

    /*private void displayHorizontalGraph() {

        ArrayList<String> categories = db.getTodaysExpenseCategories();

        ArrayList<BarEntry> amounts = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            long amount = db.getTodaysExpenseAmountByCategory(categories.get(i));
            amounts.add(new BarEntry(amount, i));
        }

        BarDataSet bardataset = new BarDataSet(amounts, "Amount");
        horBarChart.animateY(1000);
        horBarChart.setTouchEnabled(false);
        horBarChart.setDescription(" ");
        horBarChart.getXAxis().setDrawGridLines(false);
        horBarChart.getXAxis().setTextColor(Color.BLACK);
        horBarChart.getXAxis().setTextSize(10f);
        horBarChart.getXAxis().setDrawAxisLine(false);
        horBarChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        horBarChart.getAxisLeft().setDrawGridLines(false);
        horBarChart.getAxisLeft().setDrawLabels(false);
        horBarChart.getAxisLeft().setDrawAxisLine(false);
        horBarChart.getAxisLeft().setAxisMinValue(0f);
        horBarChart.getAxisLeft().setAxisMaxValue(bardataset.getYMax() * 4 / 3);
        horBarChart.getAxisRight().setDrawAxisLine(false);
        horBarChart.getAxisRight().setDrawGridLines(false);
        horBarChart.getAxisRight().setDrawLabels(false);

        BarData data = new BarData(categories, bardataset);
        bardataset.setColors(ColorTemplate.PASTEL_COLORS);
        horBarChart.setData(data);
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.stats_fragment, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
