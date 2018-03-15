package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.up;

import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva;

public class UpModel implements IUnidadProductiva.Model{
    private IUnidadProductiva.Repo upRepo = null;

    UpModel(){
    this.upRepo = new UpRepository();
}

    @Override
    public void registerUP(UnidadProductiva unidadProductivaModel) {
        upRepo.saveUp(unidadProductivaModel);
    }

    @Override
    public void updateUP(UnidadProductiva unidadProductivaModel) {
        upRepo.updateUp(unidadProductivaModel);
    }

    @Override
    public void deleteUP(UnidadProductiva unidadProductivaModel) {
        upRepo.deleteUp(unidadProductivaModel);
    }

    @Override
    public void execute() {
        upRepo.getListUPs();
    }
}
