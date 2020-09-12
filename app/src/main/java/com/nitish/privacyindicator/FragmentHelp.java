package com.nitish.privacyindicator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentHelp extends Fragment {

    private Button btn_bug, btn_cofee;
    private TextView tv_made_by, tv_built_in;
    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_help,container, false);

        setupViews();
        setupListeners();

        return root;
    }

    private void setupViews() {
        btn_bug = root.findViewById(R.id.btn_bug);
        btn_cofee = root.findViewById(R.id.btn_buy_me_coffee);
        tv_built_in = root.findViewById(R.id.tv_built_in);
        tv_made_by = root.findViewById(R.id.tv_made_by);
    }

    private void setupListeners() {
        btn_bug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBrowser("https://github.com/NitishGadangi/Privacy-Indicator-App/");
            }
        });
        btn_cofee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBrowser("https://nitishgadangi.github.io/?buy_me_coffee/");
            }
        });
        tv_built_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBrowser("https://fossunited.org/hackathon/");
            }
        });
        tv_made_by.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBrowser("https://nitishgadangi.github.io/");
            }
        });
    }

    private void openBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
