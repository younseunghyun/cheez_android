package co.cheez.cheez.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import co.cheez.cheez.App;
import co.cheez.cheez.R;
import co.cheez.cheez.activity.BaseActivity;
import co.cheez.cheez.activity.ContentViewActivity;
import co.cheez.cheez.auth.Auth;
import co.cheez.cheez.automation.view.DeclareView;
import co.cheez.cheez.http.listener.DefaultErrorListener;
import co.cheez.cheez.http.listener.DefaultListener;
import co.cheez.cheez.model.User;
import co.cheez.cheez.util.Constants;
import co.cheez.cheez.util.DeviceUtil;
import co.cheez.cheez.util.MessageUtil;

/**
 * Created by jiho on 5/29/15.
 */
public class LoginFragment extends BaseFragment
        implements View.OnClickListener, FacebookCallback<LoginResult> {

    @DeclareView(id = R.id.btn_facebook_login, click = "this")
    Button mFacebookLoginButton;

    @DeclareView(id = R.id.btn_skip_login, click = "this")
    Button mSkipLoginButton;

    CallbackManager mFacebookCallbackManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setupFacebookSdk();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_login;
    }

    private void setupFacebookSdk() {
        // setting up facebook sdk
        mFacebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mFacebookCallbackManager, this);
    }

    private void facebookLogin() {
        String[] permissions = {"public_profile", "email"};
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(permissions));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_skip_login:
                try {
                    JSONArray devices = new JSONArray()
                            .put(DeviceUtil.getDeviceInfo());
                    JSONObject params = new JSONObject()
                            .put(Constants.Keys.DEVICES, devices);
                    sendSignupRequest(params);
                } catch (JSONException e) {
                    e.printStackTrace();
                    MessageUtil.showDefaultErrorMessage();
                }
                break;
            case R.id.btn_facebook_login:
                facebookLogin();
                break;
        }
    }

    private void sendSignupRequest(JSONObject params) {
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                Constants.URLs.USER,
                params,
                new DefaultListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        super.onResponse(response);
                        User user = User.fromJsonObject(response);
                        Auth.getInstance().setUser(user);
                        try {
                            JSONObject params = new JSONObject()
                                    .put(Constants.Keys.DEVICE, DeviceUtil.getDeviceInfo());
                            sendAuthTokenRequest(params);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MessageUtil.showMessage(R.string.error_default);
                        }
                        ((BaseActivity) getActivity()).hideProgressDialog();
                    }
                },
                new DefaultErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        Log.e("error", error.toString());
                        MessageUtil.showDefaultErrorMessage();
                        ((BaseActivity) getActivity()).hideProgressDialog();
                    }
                }
        );
        ((BaseActivity) getActivity()).showProgressDialog();
        App.addRequest(request);
    }

    private void sendAuthTokenRequest(JSONObject params) {
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                Constants.URLs.AUTH_TOKEN,
                params,
                new DefaultListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        super.onResponse(response);
                        ((BaseActivity) getActivity()).hideProgressDialog();
                        try {
                            String token = response.getString(Constants.Keys.TOKEN);
                            Auth.getInstance().setAuthToken(token);
                            Intent intent = BaseActivity.getIntent(
                                    getActivity(),
                                    ContentViewActivity.class,
                                    null,
                                    IntentCompat.FLAG_ACTIVITY_CLEAR_TASK
                                            | Intent.FLAG_ACTIVITY_NEW_TASK
                            );
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            MessageUtil.showDefaultErrorMessage();
                        }
                    }
                },
                new DefaultErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        ((BaseActivity) getActivity()).hideProgressDialog();
                    }
                }
        );
        ((BaseActivity) getActivity()).showProgressDialog();
        App.addRequest(request);
    }

    // facebook login callback
    @Override
    public void onSuccess(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        ((BaseActivity) getActivity()).hideProgressDialog();

                        try {
                            JSONObject snsAccountData = new JSONObject()
                                    .put(Constants.Keys.SNS_USER_ID, object.get("id"))
                                    .put(Constants.Keys.SNS_TYPE, Constants.Integers.SNS_TYPE_FACEBOOK)
                                    .put(Constants.Keys.SNS_PROFILE_URL, "https://www.facebook.com/" + object.get("id"));
                            JSONArray snsAccounts = new JSONArray().put(snsAccountData);
                            JSONArray devices = new JSONArray().put(DeviceUtil.getDeviceInfo());
                            JSONObject userData = new JSONObject()
                                    .put(Constants.Keys.NAME, object.get("name"))
                                    .put(Constants.Keys.EMAIL, object.get("email"))
                                    .put(Constants.Keys.DEVICES, devices)
                                    .put(Constants.Keys.SNS_ACCOUNTS, snsAccounts);

                            sendSignupRequest(userData);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            MessageUtil.showDefaultErrorMessage();
                        }

                        // TODO : send request to cheez server
                    }
                });
        Bundle parameters = new Bundle();
        ((BaseActivity) getActivity()).showProgressDialog();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException e) {
        MessageUtil.showDefaultErrorMessage();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
