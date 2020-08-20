package kr.co.itforone.forestmk_android;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import butterknife.ButterKnife;

public class EndDialog extends Dialog {
    private MainActivity mContext;

    public EndDialog(@NonNull MainActivity context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_exit);
        ButterKnife.bind(this);

    }
}
