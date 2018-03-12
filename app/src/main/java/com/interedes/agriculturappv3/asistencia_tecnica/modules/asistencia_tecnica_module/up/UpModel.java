package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.up;

import com.interedes.agriculturappv3.asistencia_tecnica.models.UP;

public class UpModel implements IUnidadProductiva.Model{
    private IUnidadProductiva.Repo upRepo = null;

    UpModel(){
        this.upRepo = new UpRepository();
    }

    @Override
    public void registerUP(UP UpModel) {
        upRepo.saveUp(UpModel);
    }

    @Override
    public void updateUP(UP UpModel) {
        upRepo.updateUp(UpModel);
    }

    @Override
    public void deleteUP(UP UpModel) {
        upRepo.deleteUp(UpModel);
    }

    @Override
    public void execute() {
        upRepo.getListUPs();
    }
}
