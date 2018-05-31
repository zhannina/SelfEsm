package com.example.zsarsenbayev.emotionsgroundtruth;

import android.content.ContentValues;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<RadioGroup> radioGroups = new ArrayList<>();
    private Button finishBtn;
    private ArrayList<String> colors = new ArrayList<>();
    private Spinner colorsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        colors.add("red");
        colors.add("yellow");
        colors.add("light green");
        colors.add("blue");
        colors.add("fuchsia");
        colors.add("brown");
        colors.add("dark green");
        colors.add("aqua");
        colors.add("dark purple");
        colors.add("white");

        fillPanas();

        finishBtn = findViewById(R.id.finishBtn);
        colorsSpinner = findViewById(R.id.colorSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, colors);
        colorsSpinner.setAdapter(adapter);

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    private void fillPanas() {
        ArrayList<Object> panasItems = new ArrayList<>();
        panasItems.add("MiserablePleased");
        panasItems.add("SleepyAroused");

        LinearLayout rootLayout = findViewById(R.id.PanasItemsLinearLayout);

        for (int i = 0; i < panasItems.size(); i++) {
            LinearLayout panasContainer = new LinearLayout(this);
            panasContainer.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams panasContainerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            panasContainerParams.setMargins(0, 0, 0, 50);
            panasContainer.setLayoutParams(panasContainerParams);

            RadioGroup panasRatingRadioGroup = new RadioGroup(this);
            panasRatingRadioGroup.setOrientation(LinearLayout.HORIZONTAL);
            panasRatingRadioGroup.setGravity(Gravity.CENTER_HORIZONTAL);

            panasRatingRadioGroup.setContentDescription((CharSequence) panasItems.get(i));

            radioGroups.add(panasRatingRadioGroup);

            for (int j = 0; j < 5; j++) {
                RadioButton radioButton = new RadioButton(this);
                panasRatingRadioGroup.addView(radioButton);

                final float scale = this.getResources().getDisplayMetrics().density;
                int pixels = (int) (50 * scale + 0.5f);

                radioButton.getLayoutParams().width = pixels;
                radioButton.getLayoutParams().height = pixels;

                TypedValue typedValue = new TypedValue();
                this.getTheme().resolveAttribute(android.R.attr.listChoiceIndicatorSingle, typedValue, true);
                if (typedValue.resourceId != 0) {
                    radioButton.setButtonDrawable(null);
                    radioButton.setBackgroundResource(typedValue.resourceId);
                }
            }

            if (i == 0) {
                // Legend
                LinearLayout panasLegendLL = new LinearLayout(this);
                panasLegendLL.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams panasLegendLLParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                panasLegendLL.setLayoutParams(panasLegendLLParams);

                TextView minLabel = new TextView(this);
                minLabel.setText("Miserable");
                minLabel.setTextSize(20);
                minLabel.setGravity(Gravity.LEFT);
                LinearLayout.LayoutParams minLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                minLabelParams.setMargins(56, 0, 0, 4);
                minLabel.setLayoutParams(minLabelParams);

                panasLegendLL.addView(minLabel);

                TextView maxLabel = new TextView(this);
                maxLabel.setText("Pleased");
                maxLabel.setTextSize(20);
                maxLabel.setGravity(Gravity.RIGHT);
                LinearLayout.LayoutParams maxLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                maxLabelParams.setMargins(0, 0, 56, 4);
                maxLabel.setLayoutParams(maxLabelParams);

                panasLegendLL.addView(maxLabel);

                rootLayout.addView(panasLegendLL);
            }

            LinearLayout panasItemLL = new LinearLayout(this);
            panasItemLL.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams panasItemLLParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            panasItemLL.setLayoutParams(panasItemLLParams);

            panasContainer.addView(panasItemLL);
            panasContainer.addView(panasRatingRadioGroup);

            if (i == panasItems.size() - 1) {
                // Legend
                LinearLayout panasLegendLL = new LinearLayout(this);
                panasLegendLL.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams panasLegendLLParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                panasLegendLL.setLayoutParams(panasLegendLLParams);

                TextView minLabel = new TextView(this);
                minLabel.setText("Sleepy");
                minLabel.setTextSize(20);
                minLabel.setGravity(Gravity.LEFT);
                LinearLayout.LayoutParams minLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                minLabelParams.setMargins(56, 40, 0, 20);
                minLabel.setLayoutParams(minLabelParams);

                panasLegendLL.addView(minLabel);

                TextView maxLabel = new TextView(this);
                maxLabel.setText("Aroused");
                maxLabel.setTextSize(20);
                maxLabel.setGravity(Gravity.RIGHT);
                LinearLayout.LayoutParams maxLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                maxLabelParams.setMargins(20, 40, 56, 10);
                maxLabel.setLayoutParams(maxLabelParams);

                panasLegendLL.addView(maxLabel);

                rootLayout.addView(panasLegendLL);
            }

            rootLayout.addView(panasContainer);
        }
    }

    private void saveData(){
        int groupsCount = radioGroups.size();
        String collectedAnswers = "";
        String color;

        for (int i = 0; i < groupsCount; i++) {
            RadioGroup radioGroup = radioGroups.get(i);
            int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();

            if (selectedRadioButtonId == -1) {
                Toast incompleteSnackbar = Toast.makeText(getApplicationContext(), "Please complete all fields.", Toast.LENGTH_SHORT);
                incompleteSnackbar.show();
                return;
            } else {
                // Take modulus because ID can be any multiple of 10.
                Log.d("Niels", radioGroup.getContentDescription() + ":" + (Math.abs(selectedRadioButtonId - (i * 5)) % 10));
                collectedAnswers = collectedAnswers.concat(radioGroup.getContentDescription() + ":" + (Math.abs(selectedRadioButtonId - (i * 5)) % 10) + ";");
            }
        }

        color = colorsSpinner.getSelectedItem().toString();
        Log.d("COLOR", color);
        Log.d("COLLECTED", System.currentTimeMillis()+"");

        Log.d("COLLECTED", collectedAnswers);

        ContentValues esmData = new ContentValues();
        esmData.put(EsmProvider.EsmTable.ESM_TIMESTAMP, System.currentTimeMillis());
        esmData.put(EsmProvider.EsmTable.ESM_PERSON_COLOR, color);
        esmData.put(EsmProvider.EsmTable.ESM_ANSWER, collectedAnswers);
        getApplicationContext().getContentResolver().insert(EsmProvider.EsmTable.ESM_CONTENT_URI, esmData);
        // TODO: stop esm service
        finish();

    }
}
