package com.a65apps.kalimullinilnazrafilovich.application.contacts;

import com.a65apps.kalimullinilnazrafilovich.library.applicaiton.di.interfaces.ContactsListContainer;
import com.a65apps.kalimullinilnazrafilovich.application.scope.ContactsListScope;

import dagger.Subcomponent;

@ContactsListScope
@Subcomponent(modules = {ContactsListModule.class})
public interface ContactsListComponent extends ContactsListContainer {
}
