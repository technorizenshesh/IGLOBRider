package main.com.iglobuser.Fragments;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;

import main.com.iglobuser.R;
import main.com.iglobuser.constant.MyLanguageSession;
import main.com.iglobuser.databinding.FragmentLanguageBinding;
import main.com.iglobuser.draweractivity.BaseActivity;

public class FragmentLanguage extends BottomSheetDialogFragment {
    private FragmentLanguageBinding binding;
    private MyLanguageSession session;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog=(BottomSheetDialog)super.onCreateDialog(savedInstanceState);
        binding= DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_language,null,false);
        dialog.setContentView(binding.getRoot());
        BindView();
        return dialog;
    }

    private void BindView() {
        session= MyLanguageSession.get(getActivity());
        binding.imgBack.setOnClickListener(v->dismiss());
        binding.rdEnglish.setChecked(session.getLanguage().contains("en"));
        binding.rdArabic.setChecked(session.getLanguage().contains("ar"));
        binding.rdSpanish.setChecked(session.getLanguage().contains("es"));
        binding.rdDetch.setChecked(session.getLanguage().contains("nl"));
        binding.btnApply.setOnClickListener(v->{
            if (binding.rdArabic.isChecked()){
                session.insertLanguage("ar");
            }else if (binding.rdSpanish.isChecked()){
                session.insertLanguage("es");
            }else if (binding.rdDetch.isChecked()){
                session.insertLanguage("nl");
            } else {
                session.insertLanguage("en");
            }
            ((BaseActivity)getActivity()).onRefreshLanguage();
            dismiss();
        });
    }
}
