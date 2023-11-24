package com.example.blood_point;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.blood_point.databinding.FragmentGoogleMapFragmentBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class google_map_fragment extends Fragment implements OnMapReadyCallback{
    FragmentGoogleMapFragmentBinding binding;
    GoogleMap mMap;
    Location currentLocation;
    Marker currentMarker;
    FirebaseAuth mAuth;
    FirebaseUser cur_user;
    FirebaseDatabase fd;
    DatabaseReference db_ref,dd_ref;
    LocationRequest location_Request;
    LocationCallback location_Callback;
    boolean isTrafficEnable = true,isNightModeEnable = true,isPermissionGranted;
    int radius = 5000;  //5 km radius
    String placeName = "hospital";
    String UserName;
    Marker marker;
    // for saving donor details to display in the map
    List<String> list_address=new ArrayList<String>();
    List<String> list_blood_group=new ArrayList<String>();
    List<String> list_gender=new ArrayList<String>();
    List<String> list_name=new ArrayList<String>();
    List<String> list_phone_no=new ArrayList<String>();
    // for saving the marker Id to differentiate the markers
    ArrayList<String> markerId = new ArrayList<>();
    public google_map_fragment() {

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }
    @SuppressLint({"NonConstantResourceId", "ClickableViewAccessibility"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGoogleMapFragmentBinding.inflate(inflater, container, false);

        mAuth = FirebaseAuth.getInstance();
        db_ref = FirebaseDatabase.getInstance().getReference("USERS");
        cur_user = mAuth.getCurrentUser();
        fd = FirebaseDatabase.getInstance();
        dd_ref = fd.getReference("donor_details");
        Query single_user = db_ref.child(Objects.requireNonNull(cur_user).getUid());
        single_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserName = Objects.requireNonNull(dataSnapshot.child("NAME").getValue()).toString();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });
        binding.fabHospitals.setOnClickListener(v ->{
            getNearbyHospitals();
        });
        binding.fabDonors.setOnClickListener(v -> {
            mMap.clear();
            Blood_donors();
        });

        binding.enableTraffic.setOnClickListener(v -> {
            if (isTrafficEnable)
            {
                if (mMap != null)
                {
                    mMap.setTrafficEnabled(false);
                    isTrafficEnable = false;
                }
            }
            else
            {
                if (mMap != null)
                {
                    mMap.setTrafficEnabled(true);
                    isTrafficEnable = true;
                }
            }
        });
        binding.btnMapType.setOnClickListener(v -> {
            ContextThemeWrapper wrapper = new ContextThemeWrapper(getActivity(), R.style.comic_sans);
            PopupMenu popupMenu = new PopupMenu(wrapper, v);
            popupMenu.getMenuInflater().inflate(R.menu.map_type_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.btn_Normal:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case R.id.btn_Satellite:
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case R.id.btn_hybrid:
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                }
                return true;
            });
            popupMenu.show();
        });
        binding.btnMode.setOnClickListener(v -> {
            if (isNightModeEnable)
            {
                if (mMap != null)
                {
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireActivity(),R.raw.mapstyle_night));
                    isNightModeEnable = false;
                }
            }
            else
            {
                if (mMap != null)
                {
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireActivity(),R.raw.mspstyle_light));
                    isNightModeEnable = true;
                }
            }

        });
        binding.currentLoc.setOnClickListener(v -> getCurrentLocation());
        return binding.getRoot();
    }
    private void getNearbyHospitals() {
        try{
            Double LAT = currentLocation.getLatitude();
            Double LON = currentLocation.getLongitude();
            if(LAT == null || LON == null){
                Toast.makeText(getActivity(), "Show current Location !", Toast.LENGTH_SHORT).show();
            }
            else {
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                        + currentLocation.getLatitude() + "," + currentLocation.getLongitude()
                        + "&radius=" + radius + "&type=" + placeName + "&key=" +
                        getResources().getString(R.string.google_maps_key);
                Object dataFetch[] = new Object[2];
                dataFetch[0] = mMap;
                dataFetch[1] = url;

                FetchData fetchData = new FetchData(getHospitalIcon());
                fetchData.execute(dataFetch);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void Blood_donors(){
        List<Address> addressList = null;
        list_name.clear();
        list_blood_group.clear();
        list_gender.clear();
        list_phone_no.clear();
        list_address.clear();
        dd_ref.addValueEventListener(new ValueEventListener()
        {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {   // contains details of each user
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        list_name.add(Objects.requireNonNull(childSnapshot.child("NAME").getValue()).toString());
                        list_blood_group.add(Objects.requireNonNull(childSnapshot.child("BLOOD_GROUP").getValue()).toString());
                        list_gender.add(Objects.requireNonNull(childSnapshot.child("GENDER").getValue()).toString());
                        list_phone_no.add(Objects.requireNonNull(childSnapshot.child("PHONE_NO").getValue()).toString());
                        list_address.add(Objects.requireNonNull(childSnapshot.child("ADDRESS").getValue()).toString());
                        for(int i=0;i<list_address.size();i++) {
                            List<Address> addressList;
                            String location = list_address.get(i);
                            try{
                                Geocoder geocoder = new Geocoder(requireContext());
                                addressList = geocoder.getFromLocationName(location,1);
                                Address address = addressList.get(0);
                                LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                                String title = list_name.get(i)+"_"+list_blood_group.get(i);
                                String snippet = list_phone_no.get(i)+"_"+list_address.get(i);
                                marker = mMap.addMarker(new MarkerOptions()
                                                .position(latLng)
                                                .title(title)
                                                .snippet(snippet)
                                                .icon(getDonorIcon()));
                                assert marker != null;
                                markerId.add(marker.getId());

                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                                //Custom InfoWindow Adapter
                                mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(requireActivity(),markerId));
                                Objects.requireNonNull(mMap.addMarker(new MarkerOptions())).showInfoWindow();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("User", error.getMessage());
            }
        });
    }
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        //Check if location is enabled or not
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true;
            Toast.makeText(requireActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
            setUpGoogleMap();
        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Location Permission")
                    .setMessage("You need to Enable Location to access Google Map")
                    .setPositiveButton("Ok", (dialog, which) -> requestLocation()).create().show();
        } else {
            requestLocation();
        }
    }

    private void requestLocation() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",requireActivity().getPackageName(),"");
        intent.setData(uri);
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    private void setUpGoogleMap() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = false;
            return;
        }
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        setUpLocationUpdate();
    }

    private void setUpLocationUpdate() {
        location_Request = LocationRequest.create();
        location_Request.setInterval(10000);
        location_Request.setFastestInterval(5000);
        location_Request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        location_Callback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult location_Result) {
                for (Location location : location_Result.getLocations()) {
                    Log.d("TAG", "onLocationResult: " + location.getLongitude() + " " + location.getLatitude());
                }
                super.onLocationResult(location_Result);
            }
        };
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        try {
            mMap.clear();
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                isPermissionGranted = false;
                return;
            }
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                currentLocation = location;
                moveCameraToLocation(location);
            });
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveCameraToLocation(Location location) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new
                LatLng(location.getLatitude(), location.getLongitude()), 15);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("Name -> "+UserName)
                .icon(getCustomIcon());
        if (currentMarker != null) {
            currentMarker.remove();
        }
        currentMarker = mMap.addMarker(markerOptions);
        mMap.animateCamera(cameraUpdate);
    }
    private BitmapDescriptor getHospitalIcon() {
        Drawable background = ContextCompat.getDrawable(requireContext(), R.drawable.location_red);
        assert background != null;
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    private BitmapDescriptor getDonorIcon() {
        Drawable background = ContextCompat.getDrawable(requireContext(), R.drawable.location_donor);
        assert background != null;
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    private BitmapDescriptor getCustomIcon() {
        Drawable background = ContextCompat.getDrawable(requireContext(), R.drawable.location_user_yellow);
        assert background != null;
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}