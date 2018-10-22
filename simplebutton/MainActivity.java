package official.kyou.simplebutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText userInput;
    private TextView textView;
    //log output
    private static final String TAG = "MainActivity";
    private final String textContent = "Text Contents";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userInput = findViewById(R.id.editText);
        //clear user input
        userInput.setText("");
        Button button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        //clear text field
        textView.setText("");
        //make text view scrollable
        textView.setMovementMethod(new ScrollingMovementMethod());

        View.OnClickListener listen = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              String result = userInput.getText().toString() + "\n";
              textView.append(result);
              userInput.setText("");
            }
        };
        button.setOnClickListener(listen);
    }
    //Save the app state on screen rotate
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState: in");
        super.onRestoreInstanceState(savedInstanceState);
        //String savedString = savedInstanceState.getString(textContent);
        //textView.setText(savedString);
        // same thing as above
        textView.setText(savedInstanceState.getString(textContent));
        Log.d(TAG, "onRestoreInstanceState: out");
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onRestoreInstanceState: in");
        outState.putString(textContent, textView.getText().toString());
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onRestoreInstanceState: out");
    }
}
