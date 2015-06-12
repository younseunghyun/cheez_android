package co.cheez.cheez.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import co.cheez.cheez.App;
import co.cheez.cheez.R;
import co.cheez.cheez.activity.BaseActivity;
import co.cheez.cheez.auth.Auth;
import co.cheez.cheez.automation.view.DeclareView;
import co.cheez.cheez.http.AuthorizedMultipartRequest;
import co.cheez.cheez.http.listener.DefaultErrorListener;
import co.cheez.cheez.http.listener.DefaultListener;
import co.cheez.cheez.model.User;
import co.cheez.cheez.util.Constants;
import co.cheez.cheez.util.ImageDisplayUtil;
import co.cheez.cheez.util.MessageUtil;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileEditFragment extends BaseFragment implements View.OnClickListener {
    private static final int INTENT_SELECT_PICTURE = 151;

    @DeclareView(id = R.id.btn_finish, click = "this")
    View mFinishButton;

    @DeclareView(id = R.id.et_nickname)
    EditText mNicknameInput;

    @DeclareView(id = R.id.et_state_message)
    EditText mStateMessageInput;

    @DeclareView(id = R.id.btn_submit, click = "this")
    View mSubmitButton;

    @DeclareView(id = R.id.iv_profile_image, click = "this")
    ImageView mProfileImageView;

    private Uri mSelectedImageUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = super.onCreateView(inflater, container, savedInstanceState);

        User user = Auth.getInstance().getUser();
        mNicknameInput.setText(user.getName());
        mStateMessageInput.setText(user.getStateMessage());
        ImageDisplayUtil.displayImage(user.getDisplayImageUrl(), mProfileImageView);

        return contentView;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_profile_edit;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case INTENT_SELECT_PICTURE:
                    mSelectedImageUri = data.getData();
                    if (mSelectedImageUri != null) {
                        ImageDisplayUtil.displayImage(
                                mSelectedImageUri.toString(), mProfileImageView
                        );
                    } else {
                        mProfileImageView.setImageDrawable(null);
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_finish:
                getActivity().finish();
                return;
            case R.id.iv_profile_image:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, INTENT_SELECT_PICTURE);
                break;
            case R.id.btn_submit:

                final User user = Auth.getInstance().getUser();

                final String nickname = mNicknameInput.getText().toString();
                if (nickname.length() == 0) {
                    MessageUtil.showMessage(R.string.message_nickname_required);
                    return;
                }

                Request request = getEditProfileRequest(user, nickname);
                ((BaseActivity) getActivity()).showProgressDialog();
                App.addRequest(request, Constants.Integers.TIMEOUT_LONG);

                break;
        }
    }

    private Request<JSONObject> getEditProfileRequest(final User user, final String nickname) {
        return new AuthorizedMultipartRequest(
                Constants.URLs.EDIT_PROFILE,
                new DefaultListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        super.onResponse(response);
                        ((BaseActivity) getActivity()).hideProgressDialog();
                        getActivity().setResult(Activity.RESULT_OK);
                        User user = User.fromJsonObject(response);
                        Auth.getInstance().setUser(user);
                        getActivity().finish();
                    }
                },
                new DefaultErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        ((BaseActivity) getActivity()).hideProgressDialog();

                    }
                },
                null,
                0
        ) {
            @Override
            protected HttpEntity createHttpEntity() {

                MultipartEntityBuilder multipartEntity
                        = MultipartEntityBuilder.create();
                multipartEntity.setCharset(Charset.forName("utf-8"));
                multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                multipartEntity.addTextBody(
                        Constants.Keys.NAME,
                        nickname,
                        ContentType.APPLICATION_JSON);
                multipartEntity.addTextBody(
                        Constants.Keys.STATE_MESSAGE,
                        mStateMessageInput.getText().toString(),
                        ContentType.APPLICATION_JSON);


                if (mSelectedImageUri != null) {
                    String extension = MimeTypeMap.getFileExtensionFromUrl(mSelectedImageUri.toString());
                    try {
                        InputStream inputStream = getActivity()
                                .getContentResolver()
                                .openInputStream(mSelectedImageUri);
                        byte[] data;
                        data = IOUtils.toByteArray(inputStream);
                        InputStreamBody inputStreamBody = new InputStreamBody(
                                new ByteArrayInputStream(data),
                                "profile_image_"
                                        + user.getId()
                                        + "_"
                                        + System.currentTimeMillis()
                                        + "." + extension
                        );
                        multipartEntity.addPart(Constants.Keys.PROFILE_IMAGE, inputStreamBody);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return multipartEntity.build();
            }
        };
    }
}
