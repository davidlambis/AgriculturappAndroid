package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.up;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.interedes.agriculturappv3.R;
import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva;

import java.util.List;

public class UnidadProductivaFragment extends Fragment implements View.OnClickListener,IUnidadProductiva.View{
    IUnidadProductiva.Presenter IUpPresenter = null;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IUpPresenter = new UpPresenter(this);
        IUpPresenter.onCreate();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_unidad_productiva,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean validarCampos() {
        return false;
    }

    @Override
    public void limpiarCampos() {

    }

    @Override
    public void disableInputs() {

    }

    @Override
    public void enableInputs() {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void hideElements() {

    }

    @Override
    public void setListUps(List<UnidadProductiva> listUnidadProductivas) {

    }

    @Override
    public void requestResponseOK() {

    }

    @Override
    public void requestResponseError(String error) {

    }

    @Override
    public void onMessageOk(int colorPrimary, String msg) {

    }

    @Override
    public void onMessageError(int colorPrimary, String msg) {

    }

    @Override
    public AlertDialog showAlertDialogAddUP() {
        return null;
    }

}
