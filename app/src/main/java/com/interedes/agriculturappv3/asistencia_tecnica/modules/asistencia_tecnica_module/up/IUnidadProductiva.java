package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.up;

import android.app.AlertDialog;

import com.interedes.agriculturappv3.asistencia_tecnica.models.UP;
import com.interedes.agriculturappv3.events.RequestEvent;

import java.util.ArrayList;
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

        void setListUps(List<UP> listUps);

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

        void registerUP(UP UpModel);
        void updateUP(UP UpModel);
        void deleteUP(UP UpModel);
        void getUps();
    }

    interface Model{
        void registerUP(UP UpModel);
        void updateUP(UP UpModel);
        void deleteUP(UP UpModel);
        void execute();
    }

    interface Repo{
        List<UP> getUPs();
        void getListUPs();
        void saveUp(UP mUp);
        void updateUp(UP mUp);
        void deleteUp(UP mUp);
    }
}
