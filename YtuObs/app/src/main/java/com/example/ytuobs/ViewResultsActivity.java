package com.example.ytuobs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewResultsActivity extends AppCompatActivity {

    private PieChart pieChart;
    private Button nextQuestionButton, saveChartButton, saveCSVButton;
    private TextView questionTextView;
    private static final int REQUEST_SAVE_FILE = 1;
    private List<Map<String, Integer>> allResults;
    private List<String> questions;  // Add a list to hold question texts
    private int currentQuestionIndex = 0;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_results);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        pieChart = findViewById(R.id.pieChart);
        nextQuestionButton = findViewById(R.id.nextQuestionButton);
        saveChartButton = findViewById(R.id.saveChartButton);
        saveCSVButton = findViewById(R.id.saveCsv);  // Initialize saveCSV button
        questionTextView = findViewById(R.id.questionTextViewForResult);  // Initialize TextView

        String pollName = getIntent().getStringExtra("pollName");
        PollResults.setPollName(pollName);

        if (mUser.getEmail().endsWith("@yildiz.edu.tr")) {
            saveChartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSaveFileDialog("image/png");
                }
            });

            saveCSVButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSaveFileDialog("text/csv");
                }
            });
        } else {
            saveChartButton.setVisibility(View.GONE);
            saveCSVButton.setVisibility(View.GONE);
        }

        PollResults.getResults(new PollResults.OnResultsListener() {
            @Override
            public void onResults(List<Map<String, Integer>> results, List<String> questionTexts) {  // Modify callback to include question texts
                allResults = results;
                questions = questionTexts;  // Store question texts
                if (results != null && !results.isEmpty()) {
                    showQuestionResults(currentQuestionIndex);
                }

                nextQuestionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentQuestionIndex < allResults.size() - 1) {
                            currentQuestionIndex++;
                            showQuestionResults(currentQuestionIndex);
                        } else {
                            Toast.makeText(ViewResultsActivity.this, "No more questions.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ViewResultsActivity.this, "Sonuçlar alınırken bir hata oluştu.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showQuestionResults(int questionIndex) {
        List<PieEntry> entries = new ArrayList<>();
        Map<String, Integer> results = allResults.get(questionIndex);

        // Set question text
        String questionText = questions.get(questionIndex);  // Get the question text for the current question
        questionTextView.setText(questionText);  // Set the text in the TextView

        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Poll Results");
        dataSet.setColors(new int[]{Color.GREEN, Color.BLUE, Color.RED});
        dataSet.setValueTextSize(16f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate(); // Grafiği güncelleyin

        // Grafiği özelleştirin
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setTextSize(12f);
        legend.setTextColor(Color.BLACK);
    }

    private void showSaveFileDialog(String mimeType) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mimeType); // Kaydedilecek dosya türü
        if (mimeType.equals("image/png")) {
            intent.putExtra(Intent.EXTRA_TITLE, Integer.toString(currentQuestionIndex) + ".png"); // Varsayılan dosya adı
        } else if (mimeType.equals("text/csv")) {
            intent.putExtra(Intent.EXTRA_TITLE, "poll_results.csv"); // Varsayılan dosya adı
        }
        startActivityForResult(intent, REQUEST_SAVE_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SAVE_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                if (uri.toString().endsWith(".png")) {
                    saveImageToFile(uri);
                } else if (uri.toString().endsWith(".csv")) {
                    saveCSVToFile(uri);
                }
            }
        }
    }

    private void saveImageToFile(Uri uri) {
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                pieChart.setDrawingCacheEnabled(true);
                Bitmap bitmap = pieChart.getDrawingCache();

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

                Toast.makeText(this, "Grafik görüntüsü başarıyla kaydedildi.", Toast.LENGTH_SHORT).show();
                pieChart.setDrawingCacheEnabled(false);
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Grafik görüntüsü kaydedilirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveCSVToFile(Uri uri) {
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                BufferedWriter csvWriter = new BufferedWriter(outputStreamWriter);

                csvWriter.write("Question,Answer,Count\n");
                for (int i = 0; i < allResults.size(); i++) {
                    String question = questions.get(i);
                    Map<String, Integer> results = allResults.get(i);

                    for (Map.Entry<String, Integer> entry : results.entrySet()) {
                        csvWriter.write(question + "," + entry.getKey() + "," + entry.getValue() + "\n");
                    }
                }

                csvWriter.flush();
                csvWriter.close();
                outputStream.close();

                Toast.makeText(this, "CSV dosyası başarıyla kaydedildi.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "CSV dosyası kaydedilirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
        }
    }

}
