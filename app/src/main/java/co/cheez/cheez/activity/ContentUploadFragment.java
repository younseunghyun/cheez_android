package co.cheez.cheez.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import co.cheez.cheez.App;
import co.cheez.cheez.R;
import co.cheez.cheez.automation.view.DeclareView;
import co.cheez.cheez.fragment.BaseFragment;
import co.cheez.cheez.http.AuthorizedRequest;
import co.cheez.cheez.http.listener.DefaultErrorListener;
import co.cheez.cheez.http.listener.DefaultListener;
import co.cheez.cheez.model.OG;
import co.cheez.cheez.util.Constants;
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

                        String imageUrl = og.getImage();
                        if (imageUrl != null) {
                            ImageLoader.getInstance().displayImage(imageUrl, contentMainImageView);
                        }

                    }
                },
                new DefaultErrorListener()
        );
        App.getRequestQueue().add(request);
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

                break;
        }
    }
}
