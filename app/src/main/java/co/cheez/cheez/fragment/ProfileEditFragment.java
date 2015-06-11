package co.cheez.cheez.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import co.cheez.cheez.App;
import co.cheez.cheez.R;
import co.cheez.cheez.activity.BaseActivity;
import co.cheez.cheez.auth.Auth;
import co.cheez.cheez.automation.view.DeclareView;
import co.cheez.cheez.http.AuthorizedRequest;
import co.cheez.cheez.http.listener.DefaultErrorListener;
import co.cheez.cheez.http.listener.DefaultListener;
import co.cheez.cheez.model.User;
import co.cheez.cheez.util.Constants;
import co.cheez.cheez.util.MessageUtil;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileEditFragment extends BaseFragment implements View.OnClickListener {

    @DeclareView(id = R.id.btn_finish, click = "this")
    View mFinishButton;

    @DeclareView(id = R.id.et_nickname)
    EditText mNicknameInput;

    @DeclareView(id = R.id.et_state_message)
    EditText mStateMessageInput;

    @DeclareView(id = R.id.btn_submit, click = "this")
    View mSubmitButton;

    @DeclareView(id = R.id.iv_profile_image)
    ImageView mProfileImageView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = super.onCreateView(inflater, container, savedInstanceState);

        User user = Auth.getInstance().getUser();
        mNicknameInput.setText(user.getName());
        mStateMessageInput.setText(user.getStateMessage());

        return contentView;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_profile_edit;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_finish:
                getActivity().finish();
                return;
            case R.id.iv_profile_image:

                break;
            case R.id.btn_submit:
                try {
                    User user = Auth.getInstance().getUser();
                    ;
                    String nickname = mNicknameInput.getText().toString();
                    if (nickname.length() == 0) {
                        MessageUtil.showMessage(R.string.message_nickname_required);
                        return;
                    }
                    JSONObject params = new JSONObject();
                    params.put(Constants.Keys.NAME, nickname);
                    params.put(Constants.Keys.STATE_MESSAGE, mStateMessageInput.getText().toString());


                    Request request = new AuthorizedRequest(
                            Request.Method.PUT,
                            Constants.URLs.USER + user.getId() + "/",
                            params,
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
                                    MessageUtil.showMessage(R.string.error_nickname_duplicated);
                                }
                            }
                            );
                    ((BaseActivity) getActivity()).showProgressDialog();
                    App.addRequest(request);
                } catch (JSONException e) {
                    e.printStackTrace();
                    MessageUtil.showDefaultErrorMessage();
                }
                return;
        }
    }
}
