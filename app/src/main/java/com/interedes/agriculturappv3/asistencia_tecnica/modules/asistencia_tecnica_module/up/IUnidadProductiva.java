package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.up;

import android.app.AlertDialog;

import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva;
import com.interedes.agriculturappv3.events.RequestEvent;

import java.util.List;

public interface IUnidadProductiva {

    interface View{
        boolean validarCampos();
        void limpiarCampos();

        void disableInputs();
        void enableInputs();

        void showProgress();
        void hideProgress();
        void hideElements();

        void setListUps(List<UnidadProductiva> listUnidadProductivas);

        void requestResponseOK();
        void requestResponseError(String error);

        void onMessageOk(int colorPrimary,String msg);
        void onMessageError(int colorPrimary,String msg);

        AlertDialog showAlertDialogAddUP();
    }

    interface Presenter{
        void onCreate();
        void onDestroy();
        void onEventMainThread(RequestEvent requestEvent);
        boolean validarCampos();

        void registerUP(UnidadProductiva unidadProductivaModel);
        void updateUP(UnidadProductiva unidadProductivaModel);
        void deleteUP(UnidadProductiva unidadProductivaModel);
        void getUps();
    }

    interface Model{
        void registerUP(UnidadProductiva unidadProductivaModel);
        void updateUP(UnidadProductiva unidadProductivaModel);
        void deleteUP(UnidadProductiva unidadProductivaModel);
        void execute();
    }

    interface Repo{
        List<UnidadProductiva> getUPs();
        void getListUPs();
        void saveUp(UnidadProductiva mUnidadProductiva);
        void updateUp(UnidadProductiva mUnidadProductiva);
        void deleteUp(UnidadProductiva mUnidadProductiva);
    }
}
