package com.mhksoft.SocketDemo;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.message_et)
    EditText messageEt;
    @BindView(R.id.send_btn)
    Button sendBtn;
    @BindView(R.id.messages_rv)
    RecyclerView messagesRv;
    @BindView(R.id.reconnect_btn)
    Button reconnectBtn;
    @BindView(R.id.message_l)
    LinearLayout messageL;
    private Context mContext;
    private Socket mSocket;
    private MessageListAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addMessage("Connected!", true);
                    getSupportActionBar().setTitle("ID: " + mSocket.id());
                    reconnectBtn.setVisibility(View.GONE);
                    messageL.setVisibility(View.VISIBLE);
                }
            });
        }
    };
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addMessage("Disconnected!", true);
                    reconnectBtn.setVisibility(View.VISIBLE);
                    messageL.setVisibility(View.GONE);
                }
            });
        }
    };
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addMessage("Connection Error!", true);
                    reconnectBtn.setVisibility(View.VISIBLE);
                    messageL.setVisibility(View.GONE);
                }
            });
        }
    };
    private Emitter.Listener onConnectTimeout = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addMessage("Connection Timed Out!", true);
                    reconnectBtn.setVisibility(View.VISIBLE);
                    messageL.setVisibility(View.GONE);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;
        mSocket = App.getSocket();
        setupRecyclerView();
        setupSocket();
    }

    private void setupRecyclerView() {
        messagesRv.setLayoutManager(new LinearLayoutManager(mContext));
        messagesRv.setItemAnimator(new ScaleInAnimator(new OvershootInterpolator(1f)));
        adapter = new MessageListAdapter(messages);
        messagesRv.setAdapter(adapter);
    }

    private void addMessage(String message, Boolean isServer) {
        messages.add(new Message(
                message,
                System.currentTimeMillis(),
                isServer
        ));
        adapter.notifyItemInserted(messages.size() - 1);
        messagesRv.scrollToPosition(messages.size() - 1);
    }

    private void setupSocket() {
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeout);
        mSocket.on("receive", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            addMessage(data.getString("message"), true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        mSocket.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocket.connected()) {
            mSocket.disconnect();
            mSocket.off();
        }
    }

    @OnClick(R.id.reconnect_btn)
    public void onReconnectBtnClicked() {
        mSocket.connect();
    }

    @OnClick(R.id.send_btn)
    public void onSendBtnClicked() {
        mSocket.emit("send", messageEt.getText().toString());
        addMessage(messageEt.getText().toString(), false);
        messageEt.setText(null);
    }
}
