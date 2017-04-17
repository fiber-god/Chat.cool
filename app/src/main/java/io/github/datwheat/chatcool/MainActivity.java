package io.github.datwheat.chatcool;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import io.github.datwheat.chatcool.MessagesQuery.Data.Message;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ChatApp app = (ChatApp) getApplication();

        ArrayList<String> messages = new ArrayList<>();

        final ArrayAdapter<String> messagesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, messages);

        ListView messagesListView = (ListView) findViewById(R.id.messageListView);
        Button sendButton = (Button) findViewById(R.id.sendButton);
        final EditText messageInput = (EditText) findViewById(R.id.messageInput);

        messagesListView.setAdapter(messagesAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageInput.getText().toString();
                if (!message.isEmpty()) {
                    messagesAdapter.add(message);
                    messageInput.setText("");
                }
            }
        });

        app.apolloClient().newCall(new MessagesQuery()).enqueue(new ApolloCall.Callback<MessagesQuery.Data>() {
            @Override
            public void onResponse(@Nonnull Response<MessagesQuery.Data> response) {
                final ArrayList<String> messages = new ArrayList<>();
                for (Message message : response.data().messages()) {
                    messages.add(message.content());
                }

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messagesAdapter.addAll(messages);
                    }
                });
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        });
    }
}
