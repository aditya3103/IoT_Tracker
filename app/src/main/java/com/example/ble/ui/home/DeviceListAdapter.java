package com.example.ble.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ble.R;
import com.google.android.material.button.MaterialButton;

import java.util.Collections;
import java.util.List;

class DevicesViewHolder extends RecyclerView.ViewHolder {

    TextView deviceName;
    TextView deviceRssi;
    MaterialButton alarm;
    MaterialButton light;

    public DevicesViewHolder(View itemView) {
        super(itemView);

        deviceName = itemView.findViewById(R.id.device_name_text);
        deviceRssi = itemView.findViewById(R.id.device_rssi_text);
        alarm = itemView.findViewById(R.id.device_alarm_button);
        light = itemView.findViewById(R.id.device_light_button);

    }
}

class DevicesListAdapter extends RecyclerView.Adapter<DevicesViewHolder> {

    List<Device> list = Collections.emptyList();
    Context context;

    public DevicesListAdapter(List<Device> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public DevicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.device_card_view, parent, false);
        DevicesViewHolder viewHolder = new DevicesViewHolder(view);

        return viewHolder;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(DevicesViewHolder holder, int position) {

        Device device = list.get(position);

        holder.deviceName.setText(device.bluetoothDevice.getName());
        holder.deviceRssi.setText("RSSI: "+String.valueOf(device.rssi)+" dB");

        holder.alarm.setOnClickListener(new View.OnClickListener() {

            int state = 0;
            @Override
            public void onClick(View view) {
                if(state==0){
                    state = 1;
                    holder.alarm.setIconResource(R.drawable.ic_baseline_volume_mute_24);
                    DevicesFragment.startFeature(device.bluetoothDevice, 2,
                            state);

                } else {
                    state = 0;
                    holder.alarm.setIconResource(R.drawable.ic_baseline_volume_up_24);
                    DevicesFragment.startFeature(device.bluetoothDevice, 2, state);

                }
            }
        });

        holder.light.setOnClickListener(new View.OnClickListener() {
            int state = 0;
            @Override
            public void onClick(View view) {


                if(state==0){
                    state = 1;
                    holder.light.setIconResource(R.drawable.ic_baseline_flashlight_off_24);
                    DevicesFragment.startFeature(device.bluetoothDevice, 1,
                            state);

                } else {
                    state = 0;
                    holder.light.setIconResource(R.drawable.ic_baseline_highlight_24);
                    DevicesFragment.startFeature(device.bluetoothDevice, 1, state);

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(
            RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }
}



