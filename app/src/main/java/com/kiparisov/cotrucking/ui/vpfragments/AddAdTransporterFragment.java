package com.kiparisov.cotrucking.ui.vpfragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.kiparisov.cotrucking.R;
import com.kiparisov.cotrucking.databinding.FragmentAddTransporterAdBinding;
import com.kiparisov.cotrucking.ui.vpfragments.nestedAdFragments.CarAdTypeFragment;
import com.kiparisov.cotrucking.ui.vpfragments.nestedAdFragments.TruckAdTypeFragment;


public class AddAdTransporterFragment extends Fragment {

    private FragmentAddTransporterAdBinding binding;
    private CarAdTypeFragment carAdTypeFragment;
    private TruckAdTypeFragment truckAdTypeFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddTransporterAdBinding.inflate(getLayoutInflater());


        carAdTypeFragment = new CarAdTypeFragment();
        truckAdTypeFragment = new TruckAdTypeFragment();


        getParentFragmentManager().beginTransaction()
                .replace(R.id.transporters_ad_fragment_container, carAdTypeFragment).commit();

        binding.rgTransporters.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Fragment fragment;
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.transporter_car:
                        fragment = carAdTypeFragment;
                        break;
                    default:
                        fragment = truckAdTypeFragment;
                }
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.transporters_ad_fragment_container, fragment).commit();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}