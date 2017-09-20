package com.example.user.emergencyhealthcare.database.adapter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.emergencyhealthcare.R;
import com.example.user.emergencyhealthcare.actvities.HospitalDetails;
import com.example.user.emergencyhealthcare.actvities.MainActivity;
import com.example.user.emergencyhealthcare.model.Hospital;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by User on 4/25/2016.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    ArrayList<Hospital> items = new ArrayList<>();
    MainActivity ctx;
    Hospital hitem;
    int position;
    Hospital calldetails;
    LatLng latLng;
    double latitude, longitude;
    String username;

    public MainAdapter(MainActivity ctx, ArrayList<Hospital> items, double latitude, double longitude, String username) {
        this.ctx = ctx;
        this.items = items;
        this.latitude = latitude;
        this.longitude = longitude;
        this.username = username;
    }

    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hospital_row_items, parent, false);
        ViewHolder view = new ViewHolder(v);
        return view;
    }

    @Override
    public void onBindViewHolder(MainAdapter.ViewHolder holder, int position) {

        hitem = items.get(position);
        this.position = position;
        holder.hospName.setText(hitem.getName());
        double distance = hitem.getDistance() / 1000;
        DecimalFormat f = new DecimalFormat("#0.00");
        String roundedOff = f.format(distance);
        holder.distance.setText(" " + roundedOff + " km");
       /* if (Objects.equals(hitem.getPhoneno(), "0")) {
            holder.phoneNo.setText("  Not Updated");
            //      holder.call.setClickable(false);
        } */
        if(hitem.getPhoneno().equals("0"))
        {
            holder.phoneNo.setText("  Not Updated");
            //      holder.call.setClickable(false);
        }
        else {
            holder.phoneNo.setText("  " + hitem.getPhoneno());
            String phno = hitem.getPhoneno();
            //     holder.call.setClickable(true);
        }
  /*      holder.tlatitude.setText(String.valueOf(hitem.getLatitude()));
        holder.tlatitude.setText(String.valueOf(hitem.getLongitude()));*/
        holder.id.setText(String.valueOf(position));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView hospName;
        public TextView distance;
        public TextView phoneNo, id;
        public ImageView image;
        Button navigate, call;
        //      public MaterialRippleLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            hospName = (TextView) v.findViewById(R.id.activitymain_hospname);
            //           like = (TextView) v.findViewById(R.id.like);
            distance = (TextView) v.findViewById(R.id.main_distance);
            phoneNo = (TextView) v.findViewById(R.id.activitymain_phoneno);
            navigate = (Button) v.findViewById(R.id.activitymain_navigate);
            call = (Button) v.findViewById(R.id.activitymain_call);
            id = (TextView)v.findViewById(R.id.id);


            //   lyt_parent = (MaterialRippleLayout) v.findViewById(R.id.lyt_parent);

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (phoneNo.getText().toString().equals("  Not Updated")) {
                        Toast.makeText(ctx, "Number not updated", Toast.LENGTH_SHORT).show();

                    } else {
                        Uri uri = Uri.parse("tel:" + Uri.encode(phoneNo.getText().toString()));
                        Intent intent = new Intent(Intent.ACTION_CALL, uri);
                        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        v.getContext().startActivity(intent);
                    }
                }
            });

            navigate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = Integer.valueOf(id.getText().toString());
                    Hospital itemDetails = new Hospital();
                    itemDetails = items.get(pos);
                    Bundle args = new Bundle();
                    /*double lat = Double.parseDouble(tlatitude.getText().toString());
                    double lon = Double.parseDouble(tlongitude.getText().toString());*/
                    latLng = new LatLng(itemDetails.getLatitude(), itemDetails.getLongitude());
                    args.putParcelable("LATLNG", latLng);
                    Intent i = new Intent(ctx, HospitalDetails.class);
                    i.putExtras(args);
                    i.putExtra("LATITUDE", latitude);
                    i.putExtra("LONGITUDE", longitude);
                    i.putExtra("USERNAME", username);
                    i.putExtra("NUMBER", phoneNo.getText().toString());
                    v.getContext().startActivity(i);

                }
            });
        }

    }
}
