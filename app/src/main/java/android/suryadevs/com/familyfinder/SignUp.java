package android.suryadevs.com.familyfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final EditText name_value=(EditText)findViewById(R.id.editName);
        final EditText email_value=(EditText)findViewById(R.id.email_sign_up);
        final EditText phone_value=(EditText)findViewById(R.id.edit_number);
        final EditText password_value=(EditText)findViewById(R.id.password_sign_up);
        Button signUp = (Button)findViewById(R.id.sign_up_button);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Firebase myFirebaseRef = new Firebase("https://familylist-suryadevs.firebaseio.com/");
                String email=email_value.getText().toString();
                String password=password_value.getText().toString();
                Log.d("email: ", email);

                myFirebaseRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {

                        Log.d("uid: ", String.valueOf(result.get("uid")));
                        String myuniqid = result.get("uid").toString();

                        Firebase postRef = myFirebaseRef.child("users");
                        Firebase my = postRef.child(myuniqid);
                        String name=name_value.getText().toString();
                        my.child("name").setValue(name);
                        String  phone=phone_value.getText().toString();
                        my.child("phone").setValue(phone);
                        my.child("photo").setValue("http://emojipedia-us.s3.amazonaws.com/cache/b5/3d/b53d51c04f148e9609d0cc61da84e8bf.png");
                        Toast.makeText(SignUp.this, "Account created: " + result.get("uid"), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUp.this, StartActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        // there was an error
                        Toast.makeText(SignUp.this, firebaseError.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("Error msg: ", firebaseError.toString());
                    }
                });

            }
        });

    }
}
