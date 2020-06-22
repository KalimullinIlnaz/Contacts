package com.a65apps.kalimullinilnazrafilovich.library.applicaiton.presenters;

import com.a65apps.kalimullinilnazrafilovich.entities.Point;
import com.a65apps.kalimullinilnazrafilovich.entities.Route;
import com.a65apps.kalimullinilnazrafilovich.interactors.location.ContactLocationInteractor;
import com.a65apps.kalimullinilnazrafilovich.interactors.route.RouteInteractor;
import com.a65apps.kalimullinilnazrafilovich.library.applicaiton.mapper.GoogleDTOMapper;
import com.a65apps.kalimullinilnazrafilovich.library.applicaiton.views.FullMapView;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@InjectViewState
public class MapRoutePresenter extends MvpPresenter<FullMapView> {
    private CompositeDisposable compositeDisposable;

    private final ContactLocationInteractor contactLocationInteractor;
    private final RouteInteractor routeInteractor;

    public MapRoutePresenter(ContactLocationInteractor contactLocationInteractor, RouteInteractor routeInteractor){
        compositeDisposable = new CompositeDisposable();

        this.contactLocationInteractor = contactLocationInteractor;
        this.routeInteractor = routeInteractor;
    }

    public void showMarkers(){
        compositeDisposable
            .add(contactLocationInteractor.loadAllContactLocations()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                (list) -> getViewState().showMarkers(list),
                (Throwable::printStackTrace)
            ));
    }

    public void showRoute(String from,String to){
        compositeDisposable
            .add(
                    routeInteractor.loadRoute(from, to)
                    .subscribeOn(Schedulers.io())
                    .map( (route) -> getListLatLnt(route))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            (dots) ->{
                                if (dots.isEmpty()) getViewState().showMessageNoRoute();
                                else getViewState().showRoute(dots);
                            },
                            (Throwable::printStackTrace)
                    )
            );
    }

    private List<LatLng> getListLatLnt(Route route){
        if (route.getPoints().isEmpty()) return new ArrayList<>();
        else {
            List<LatLng> latLngs = new ArrayList<>();
            for (Point point: route.getPoints()) {
                latLngs.add(new LatLng(point.getLatitude(), point.getLongitude()));
            }
            return latLngs;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        compositeDisposable.dispose();
    }
}
