package com.stevecavallin.cipchat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steve on 24/07/14.
 */
public class ChatArrayAdapter extends ArrayAdapter {

    public ChatArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    private TextView chatText;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private LinearLayout singleMessageContainer;

    @Override
    public void add(Object object) {
        chatMessageList.add((ChatMessage)object);
        super.add(object);
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_message, parent, false);
        }
        singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
        ChatMessage chatMessageObj = getItem(position);
        chatText = (TextView) row.findViewById(R.id.singleMessage);
        chatText.setText(chatMessageObj.message+"\n"+chatMessageObj.dataora, TextView.BufferType.SPANNABLE);
        Spannable span = (Spannable) chatText.getText();
        span.setSpan(new ForegroundColorSpan(Color.parseColor("#ABCDEF")), chatMessageObj.message.length() , chatText.getText().length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        span.setSpan(new RelativeSizeSpan(0.6f), chatMessageObj.message.length() , chatText.getText().length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        chatText.setBackgroundResource(chatMessageObj.left ? R.drawable.fumetto_ric : R.drawable.fumetto_inv);
        singleMessageContainer.setGravity(chatMessageObj.left ? Gravity.LEFT : Gravity.RIGHT);
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }


}
