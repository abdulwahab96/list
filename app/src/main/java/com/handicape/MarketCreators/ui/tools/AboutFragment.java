package com.handicape.MarketCreators.ui.tools;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.handicape.MarketCreators.R;
import com.tomer.fadingtextview.FadingTextView;

public class AboutFragment extends Fragment {

    private AboutViewModel aboutViewModel;
    FadingTextView fadingTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        aboutViewModel =
                ViewModelProviders.of(this).get(AboutViewModel.class);
        View root = inflater.inflate(R.layout.fragment_about, container, false);

        fadingTextView = (FadingTextView)root.findViewById(R.id.fadinT);
        fadingTextView.setTimeout(FadingTextView.SECONDS,FadingTextView.SECONDS);

        final Button twiBt = (Button) root.findViewById(R.id.twi);
        twiBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTwitter();
            }
        });

        final Button faceBt = (Button) root.findViewById(R.id.face);
        faceBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFacebook();
            }
        });

//        final TextView textView = root.findViewById(R.id.text_tools);
//        aboutViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
////                textView.setText(s);
//            }
//        });
        return root;
    }

    public void openTwitter(){
        Intent twitter = new Intent(Intent.ACTION_VIEW);
        twitter.setData(Uri.parse("https://l.facebook.com/l.php?u=https%3A%2F%2Ftwitter.com%2FOdesa10Dev%3Fs%3D09%26fbclid%3DIwAR0csjRThPkHBRIn2EwhG3vlWFhIt_9rJHAbGq-whIdtB3qEXOGwK8DhGe8&h=AT1R70QsGVDABA8y5Obj7l7dKuRDX0RlDuYjyY9ccb-6Fa7jqxUSPcJeLLsR_zUuH9NIAu-OMdV1q0h1zvGEvxMhFk7ksvOrlQZl1rfoYBj40m4xxxmkShPPcIAAQeW4OMB7"));
        if (twitter.resolveActivity(getActivity().getPackageManager()) !=null) {
            startActivity(twitter);
        }
    }

    public void openFacebook(){
        Intent face = new Intent(Intent.ACTION_VIEW);
        face.setData(Uri.parse("https://www.facebook.com/Odesa-1171416399674057/"));
        if (face.resolveActivity(getActivity().getPackageManager()) !=null) {
            startActivity(face);
        }

    }
}