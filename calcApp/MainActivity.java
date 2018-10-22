package official.kyou.simplecalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private EditText result;
    private EditText newNumber;
    private TextView displayOperation;

    // Variables to hold operands and type of calculation
    private Double operand1;
    private String pendingOp = "=";

    private static final String STATE_PENDING_OPERATION = "PendingOperation";
    private static final String STATE_OPERAND1 = "Operand1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = findViewById(R.id.result);
        newNumber = findViewById(R.id.newNumber);
        displayOperation = findViewById(R.id.operation);
        // Calculator buttons
        // Digits
        Button button0 = findViewById(R.id.button0);
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        Button button4 = findViewById(R.id.button4);
        Button button5 = findViewById(R.id.button5);
        Button button6 = findViewById(R.id.button6);
        Button button7 = findViewById(R.id.button7);
        Button button8 = findViewById(R.id.button8);
        Button button9 = findViewById(R.id.button9);

        //Operands
        Button buttonDecimal = findViewById(R.id.decimal);
        Button buttonNegative = findViewById(R.id.negative);
        Button buttonEquals = findViewById(R.id.equals);
        Button buttonDivide = findViewById(R.id.divide);
        Button buttonMultiply = findViewById(R.id.multiply);
        Button buttonMinus = findViewById(R.id.minus);
        Button buttonPlus = findViewById(R.id.plus);


        // When button is tapped reference passed
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            // All widgets are views so any widget tapped can be passed as a parameter
            public void onClick(View view) {
                // Not all widgets have text so we set it to a button which does
                Button b = (Button) view;
                newNumber.append(b.getText().toString());
            }
        };
        // Set onClickListener for the digits and decimal
        button0.setOnClickListener(listener);
        button1.setOnClickListener(listener);
        button2.setOnClickListener(listener);
        button3.setOnClickListener(listener);
        button4.setOnClickListener(listener);
        button5.setOnClickListener(listener);
        button6.setOnClickListener(listener);
        button7.setOnClickListener(listener);
        button8.setOnClickListener(listener);
        button9.setOnClickListener(listener);
        buttonDecimal.setOnClickListener(listener);

        View.OnClickListener opListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Type cast to button
                Button b = (Button) view;
                // Read text and assign to variable op
                String op = b.getText().toString();
                // Read the number in newNumber
                String value = newNumber.getText().toString();
                // Check to see if there is input before performing operation
                try {
                    Double doubleValue = Double.valueOf(value);
                    performOperation(doubleValue, op);
                } catch (NumberFormatException e) {
                    newNumber.setText("");
                }
                pendingOp = op;
                displayOperation.setText(pendingOp);
            }
        };
        // Set listeners for operands
        buttonEquals.setOnClickListener(opListener);
        buttonDivide.setOnClickListener(opListener);
        buttonMultiply.setOnClickListener(opListener);
        buttonMinus.setOnClickListener(opListener);
        buttonPlus.setOnClickListener(opListener);
        // Set listener for Negative
        buttonNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = newNumber.getText().toString();
                if (value.length() == 0) {
                    newNumber.setText("-");
                } else {
                    try {
                        Double doubleValue = Double.valueOf(value);
                        doubleValue *= -1;
                        newNumber.setText(doubleValue.toString());
                    } catch (NumberFormatException e) {
                        // number was "-" or "." so clear
                        newNumber.setText("");
                    }
                }
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_PENDING_OPERATION, pendingOp);
        if (operand1 != null) {
            outState.putDouble(STATE_PENDING_OPERATION, operand1);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pendingOp = savedInstanceState.getString(STATE_PENDING_OPERATION);
        operand1 = savedInstanceState.getDouble(STATE_OPERAND1);
        displayOperation.setText(pendingOp);
    }

    // Make the necessary calculations
    private void performOperation(Double value, String operation) {
        // Check if operand1 is null, if it is skip until not null
        if (operand1 == null) {
            operand1 = value;
        }
        // Not null so continue with the calculation
        else {
            if (pendingOp.equals("=")) {
                pendingOp = operation;
            }
            // Test cases of buttons
            switch (pendingOp) {
                case "=":
                    operand1 = value;
                    break;
                case "/":
                    if (value == 0) {
                        operand1 = 0.0;
                    } else {
                        operand1 /= value;
                    }
                    break;
                case "*":
                    operand1 *= value;
                    break;
                case "-":
                    operand1 -= value;
                    break;
                case "+":
                    operand1 += value;
                    break;
            }
        }
        result.setText(operand1.toString());
        // Clear input
        newNumber.setText("");
    }
}
