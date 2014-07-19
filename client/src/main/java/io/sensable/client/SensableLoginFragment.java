package io.sensable.client;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import io.sensable.model.UserLogin;

/**
 * Created by madine on 01/07/14.
 */
public class SensableLoginFragment extends DialogFragment {

    private EditText loginUsername;
    private EditText loginPassword;
    private Button submitButton;
    private SensableLoginListener sensableLoginListener;

    public static interface SensableLoginListener {
        public void onConfirmed(UserLogin userLogin);
    }

    public SensableLoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sensable_login_layout, null);

        getDialog().setTitle(getActivity().getString(R.string.dialogTitleSensableLogin));

        loginUsername = (EditText) view.findViewById(R.id.login_username_field);
        loginPassword = (EditText) view.findViewById(R.id.login_password_field);

        addListenerOnButton(view);

        return view;
    }

    public SensableLoginListener getRecipientPickerListener() {
        return sensableLoginListener;
    }

    public void setSensableLoginListener(SensableLoginListener sensableLoginListener) {
        this.sensableLoginListener = sensableLoginListener;
    }


    public void addListenerOnButton(View view) {

        submitButton = (Button) view.findViewById(R.id.login_submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (loginUsername.getText().toString().length() > 0 && loginPassword.getText().toString().length() > 0) {
                    UserLogin userLogin = new UserLogin(loginUsername.getText().toString(), loginPassword.getText().toString());
                    sensableLoginListener.onConfirmed(userLogin);
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), "Username and password required", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

}
