package com.a65apps.kalimullinilnazrafilovich.library.applicaiton.presenters;

import com.a65apps.kalimullinilnazrafilovich.library.applicaiton.repositories.GeocodeRepository;
import com.a65apps.kalimullinilnazrafilovich.library.applicaiton.models.Location;
import com.a65apps.kalimullinilnazrafilovich.library.applicaiton.models.YandexAddressResponseDTO;
import com.a65apps.kalimullinilnazrafilovich.library.applicaiton.repositories.DataBaseRepository;
import com.a65apps.kalimullinilnazrafilovich.library.applicaiton.views.MapView;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.android.gms.maps.model.LatLng;

import entities.Contact;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@InjectViewState
public class MapPresenter extends MvpPresenter<MapView> {
    private CompositeDisposable compositeDisposable;

    private DataBaseRepository dataBaseRepository;
    private GeocodeRepository geocodeRepository;


    public MapPresenter(DataBaseRepository dataBaseRepository, GeocodeRepository geocodeRepository){
        compositeDisposable = new CompositeDisposable();

        this.dataBaseRepository = dataBaseRepository;
        this.geocodeRepository = geocodeRepository;
    }

    public void showMarker(String id){
        compositeDisposable
            .add(Single.fromCallable(() -> getData(id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    (contact) -> getViewState().showMapMarker(new LatLng(contact.getLatitude(),contact.getLongitude())),
                    (Throwable::printStackTrace)
                )
            );
    }

    private Contact getData(String id) {
        return dataBaseRepository.getContactById(id);
    }

    public void getLocationMapClick(String id,LatLng point) {
        String coordinate = point.longitude + "," + point.latitude;

        compositeDisposable.add(geocodeRepository.getAddress(coordinate)
                .subscribeOn(Schedulers.io())
                .doOnSuccess( (dto) ->
                        {
                            String address = getFullAddress(dto);
                            if (!address.equals("")){
                                dataBaseRepository.insertContact(
                                        new Location(id,address,point.latitude,
                                        point.longitude));
                            }
                        }
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (address) -> getViewState().showMapMarker(point),
                        (Throwable::printStackTrace)
                ));
    }

    private String getFullAddress(YandexAddressResponseDTO yandexAddressResponseDTO){
        try {
            return yandexAddressResponseDTO.getResponse()
                    .getGeoObjectCollection().getFeatureMember().get(0).getGeoObject()
                    .getMetaDataProperty().getGeocoderMetaData().getText();
        }catch (IndexOutOfBoundsException e){
            return "";
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}