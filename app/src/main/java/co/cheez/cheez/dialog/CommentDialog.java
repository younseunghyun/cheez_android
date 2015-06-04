package co.cheez.cheez.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import co.cheez.cheez.App;
import co.cheez.cheez.R;
import co.cheez.cheez.adapter.CommentRecyclerViewAdapter;
import co.cheez.cheez.automation.view.DeclareView;
import co.cheez.cheez.automation.view.ViewMapper;
import co.cheez.cheez.http.AuthorizedRequest;
import co.cheez.cheez.http.listener.DefaultErrorListener;
import co.cheez.cheez.http.listener.DefaultListener;
import co.cheez.cheez.model.Comment;
import co.cheez.cheez.util.Constants;
import co.cheez.cheez.util.DialogUtil;
import co.cheez.cheez.util.MessageUtil;

/**
 * Created by jiho on 5/25/15.
 */
public class CommentDialog implements View.OnClickListener {
    private long mPostId;
    private boolean waitingResponse = false;
    private Dialog mDialog;
    private Context mContext;

    @DeclareView(id = R.id.btn_close_dialog, click = "this")
    View mCloseButton;

    @DeclareView(id = R.id.rv_comment_list)
    RecyclerView mCommentListRecyclerView;

    @DeclareView(id = R.id.et_comment)
    EditText mCommentInput;

    @DeclareView(id = R.id.btn_comment_submit, click = "this")
    View mCommentSubmitButton;

    @DeclareView(id = R.id.progress_comment_loading)
    View mCommentLoadingProgressView;

    @DeclareView(id = R.id.tv_empty_label)
    View mEmptyLabel;

    private LinearLayoutManager mLayoutManager;
    private CommentRecyclerViewAdapter mAdapter;


    public CommentDialog(Context context) {
        mLayoutManager = new LinearLayoutManager(mContext);
        mAdapter = new CommentRecyclerViewAdapter();
        mContext = context;
    }

    public CommentDialog setPostId(long postId) {
        mPostId = postId;
        mAdapter.getCommentList().clear();
        mAdapter.notifyDataSetChanged();
        return this;
    }

    public Dialog getDialog() {
        if (mDialog == null) {

            View dialogView = ViewMapper.inflateLayout(mContext, this, R.layout.dialog_comment);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                    .setView(dialogView);

            mCommentListRecyclerView.setLayoutManager(mLayoutManager);
            mCommentListRecyclerView.setAdapter(mAdapter);

            mCommentListRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (mLayoutManager.findLastVisibleItemPosition() == mAdapter.getItemCount() - 1) {
                        requestCommentData();
                    }
                }
            });


            mDialog = builder.create();

            mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    requestCommentData();
                }
            });

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = mDialog.getWindow();
            lp.copyFrom(window.getAttributes());
            //This makes the dialog take up the full width
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }
        return mDialog;
    }

    private void requestCommentData() {
        if (waitingResponse) {
            return;
        }
        waitingResponse = true;
        long lastCommentId = 0;
        if (mAdapter.getItemCount() > 0) {
            lastCommentId = mAdapter.getCommentList().get(mAdapter.getItemCount() - 1).getId();
        }
        Request request = new AuthorizedRequest(
                Request.Method.GET,
                Constants.URLs.COMMENT
                        + "?post_id=" + mPostId
                        + "&last_id=" + lastCommentId,
                null,
                new DefaultListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        super.onResponse(response);

                        mCommentLoadingProgressView.setVisibility(View.GONE);

                        try {
                            JSONArray results = response.getJSONArray(Constants.Keys.RESULTS);
                            int length = results.length();
                            List<Comment> commentList = mAdapter.getCommentList();
                            for (int i = 0; i < length; i++) {
                                Comment comment
                                        = Comment.fromJsonString(results.getString(i));
                                commentList.add(comment);
                            }
                            mAdapter.notifyDataSetChanged();
                            if (mAdapter.getItemCount() == 0) {
                                mEmptyLabel.setVisibility(View.VISIBLE);
                            } else {
                                mEmptyLabel.setVisibility(View.GONE);
                            }

                            if (length > 0) {
                                waitingResponse = false;
                            }
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
                        mCommentLoadingProgressView.setVisibility(View.GONE);
                        mDialog.dismiss();
                    }
                }
        );

        mCommentLoadingProgressView.setVisibility(View.VISIBLE);
        App.addRequest(request);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close_dialog:
                mDialog.dismiss();
                break;
            case R.id.btn_comment_submit:

                String comment = mCommentInput.getText().toString();
                if (comment.length() == 0) {
                    MessageUtil.showMessage(R.string.message_comment_required);
                    return;
                }
                sendWriteCommentRequest(comment);

                break;
        }
    }

    private void sendWriteCommentRequest(String commentBody) {
        try {
            JSONObject params = new JSONObject()
                    .put(Constants.Keys.POST_ID, mPostId)
                    .put(Constants.Keys.COMMENT, commentBody);

            final Dialog progressDialog = DialogUtil.getProgressDialog(mContext, false);
            progressDialog.show();
            Request request = new AuthorizedRequest(
                    Request.Method.POST,
                    Constants.URLs.COMMENT,
                    params,
                    new DefaultListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            super.onResponse(response);
                            progressDialog.dismiss();
                            mCommentInput.setText("");
                            Comment comment = Comment.fromJsonObject(response);
                            mAdapter.getCommentList().add(0, comment);
                            mAdapter.notifyDataSetChanged();
                            mEmptyLabel.setVisibility(View.GONE);
                        }
                    },
                    new DefaultErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            super.onErrorResponse(error);

                            progressDialog.dismiss();

                        }
                    });

            App.addRequest(request);


        } catch (JSONException e) {
            e.printStackTrace();
            MessageUtil.showDefaultErrorMessage();
        }
    }
}
