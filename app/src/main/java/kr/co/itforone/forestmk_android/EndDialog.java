package kr.co.itforone.forestmk_android;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import butterknife.ButterKnife;

public class EndDialog extends Dialog {
    private Activity mContext;

    public EndDialog(Activity context) {
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
