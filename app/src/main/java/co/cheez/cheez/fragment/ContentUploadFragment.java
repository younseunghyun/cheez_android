package co.cheez.cheez.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.cheez.cheez.App;
import co.cheez.cheez.R;
import co.cheez.cheez.activity.BaseActivity;
import co.cheez.cheez.automation.view.DeclareView;
import co.cheez.cheez.http.AuthorizedRequest;
import co.cheez.cheez.http.listener.DefaultErrorListener;
import co.cheez.cheez.http.listener.DefaultListener;
import co.cheez.cheez.model.OG;
import co.cheez.cheez.util.Constants;
import co.cheez.cheez.util.ImageDisplayUtil;
import co.cheez.cheez.util.MessageUtil;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContentUploadFragment extends BaseFragment implements View.OnClickListener {
    @DeclareView(id = R.id.et_url)
    private EditText urlInput;

    @DeclareView(id = R.id.btn_request_og, click = "this")
    private Button requestOGButton;

    @DeclareView(id = R.id.et_title)
    private EditText titleInput;

    @DeclareView(id = R.id.et_subtitle)
    private EditText subtitleInput;

    @DeclareView(id = R.id.et_tags)
    private EditText tagsInput;

    @DeclareView(id = R.id.iv_content_main)
    private ImageView contentMainImageView;

    @DeclareView(id = R.id.btn_submit, click = "this")
    private Button submitButton;

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_content_upload;
    }

    private String mSelectedImageUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = super.onCreateView(inflater, container, savedInstanceState);



        return contentView;
    }

    private void getOgData(String url) throws JSONException {
        JSONObject params = new JSONObject().put(Constants.Keys.URL, url);
        Request request = new AuthorizedRequest(
                Request.Method.POST,
                Constants.URLs.OG,
                params,
                new DefaultListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        super.onResponse(response);

                        OG og = OG.fromJsonObject(response);
                        titleInput.setText(og.getTitle());
                        subtitleInput.setText(og.getDescription());
                        mSelectedImageUrl = og.getImage();

                        if (mSelectedImageUrl != null) {
                            ImageDisplayUtil.displayImage(mSelectedImageUrl, contentMainImageView);
                        }

                        ((BaseActivity)getActivity()).hideProgressDialog();

                    }
                },
                new DefaultErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        ((BaseActivity)getActivity()).hideProgressDialog();
                    }
                }
        );
        ((BaseActivity)getActivity()).showProgressDialog();
        App.addRequest(request);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_request_og:
                try {
                    getOgData(urlInput.getText().toString());
                } catch (JSONException e) {
                    MessageUtil.showDefaultErrorMessage();
                    e.printStackTrace();
                }
                break;
            case R.id.btn_submit:
                sendWritePostRequest();

                break;
        }
    }

    private void sendWritePostRequest() {
        String url = urlInput.getText().toString();
        String title = titleInput.getText().toString();
        String subtitle = subtitleInput.getText().toString();
        String[] tags = tagsInput.getText().toString().split(" ");

        // form validation
        if (url.length() == 0) {
            MessageUtil.showMessage(R.string.message_url_required);
            urlInput.requestFocus();
            return;
        }
        if (title.length() == 0) {
            MessageUtil.showMessage(R.string.message_title_required);
            titleInput.requestFocus();
            return;
        }

        JSONObject params = null;
        try {
            params = new JSONObject()
                    .put(Constants.Keys.SOURCE_URL, url)
                    .put(Constants.Keys.TITLE, title);
            if (subtitle.length() > 0) {
                params.put(Constants.Keys.SUBTITLE, subtitle);
            }
            if (mSelectedImageUrl != null
                    && mSelectedImageUrl.length() > 0) {
                params.put(Constants.Keys.IMAGE_URL, mSelectedImageUrl);
            }
            if (tags.length > 0) {
                JSONArray tagsJsonArray = new JSONArray();
                for (String tag:tags) {
                    tag = tag.trim();
                    if (tag.length() > 0) {
                        tagsJsonArray.put(tag);
                    }
                }
                params.put(Constants.Keys.TAGS, tagsJsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            MessageUtil.showDefaultErrorMessage();
        }

        Request request = new AuthorizedRequest(
                Request.Method.POST,
                Constants.URLs.POST,
                params,
                new DefaultListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        super.onResponse(response);
                        ((BaseActivity)getActivity()).hideProgressDialog();
                    }
                },
                new DefaultErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        ((BaseActivity)getActivity()).hideProgressDialog();
                    }
                }
        );
        ((BaseActivity)getActivity()).showProgressDialog();
        App.addRequest(request, Constants.Integers.TIMEOUT_LONG);
    }

    public void setSourceUrl(String url) {
        urlInput.setText(url);
        try {
            getOgData(url);
        } catch (JSONException e) {
            MessageUtil.showDefaultErrorMessage();
            e.printStackTrace();
        }
    }
}
