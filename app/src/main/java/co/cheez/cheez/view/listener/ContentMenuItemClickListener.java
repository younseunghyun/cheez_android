package co.cheez.cheez.view.listener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import co.cheez.cheez.App;
import co.cheez.cheez.R;
import co.cheez.cheez.http.AuthorizedRequest;
import co.cheez.cheez.http.listener.DefaultErrorListener;
import co.cheez.cheez.http.listener.DefaultListener;
import co.cheez.cheez.model.Post;
import co.cheez.cheez.util.Constants;
import co.cheez.cheez.util.MessageUtil;

/**
 * Created by jiho on 5/24/15.
 */
public class ContentMenuItemClickListener
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private Post mPost;

    public ContentMenuItemClickListener(Post post) {
        mPost = post;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_report:
                showReportDialog(v);
                break;
            case R.id.btn_show_tags:
                MessageUtil.showMessage(R.string.message_prepare);
                break;
        }
    }

    private void showReportDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_report, null);
        ((TextView) dialogView.findViewById(R.id.tv_title)).setText(mPost.getTitle());

        // TODO : change to format string resource
        ((TextView) dialogView.findViewById(R.id.tv_username))
                .setText("게시자: " + mPost.getUser().getDisplayName());
        builder.setView(dialogView);
        final EditText reportBodyInput = (EditText) dialogView.findViewById(R.id.et_report_body);

        final Dialog dialog = builder.create();
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_close_dialog:
                        dialog.dismiss();
                        break;
                    case R.id.btn_report_submit:
                        String reportBody = reportBodyInput.getText().toString();
                        if (reportBody.length() == 0) {
                            MessageUtil.showMessage(R.string.message_enter_report_body);
                            reportBodyInput.requestFocus();
                            return;
                        }
                        try {
                            JSONObject params = new JSONObject()
                                    .put(Constants.Keys.POST_ID, mPost.getId())
                                    .put(Constants.Keys.REASON, reportBody);
                            Request request = new AuthorizedRequest(
                                    Request.Method.POST,
                                    Constants.URLs.REPORT,
                                    params,
                                    new DefaultListener() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            super.onResponse(response);
                                            MessageUtil.showMessage(R.string.message_reported);
                                        }
                                    },
                                    new DefaultErrorListener()
                                    );
                            App.addRequest(request);
                            dialog.dismiss();
                        } catch (JSONException e) {
                            MessageUtil.showDefaultErrorMessage();
                            e.printStackTrace();
                        }

                        break;
                }
            }
        };
        dialogView.findViewById(R.id.btn_report_submit).setOnClickListener(listener);
        dialogView.findViewById(R.id.btn_close_dialog).setOnClickListener(listener);
        dialog.show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.tb_save:
                mPost.setSaved(isChecked);
                break;
        }
    }
}
