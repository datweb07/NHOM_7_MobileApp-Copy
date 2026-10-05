package com.rescuefarm.ui.checkout;
import android.content.Context;import androidx.annotation.NonNull;import androidx.lifecycle.*;import com.rescuefarm.data.repository.*;
public class CheckoutViewModelFactory implements ViewModelProvider.Factory{private final Context c;public CheckoutViewModelFactory(Context c){this.c=c.getApplicationContext();}@NonNull public <T extends ViewModel>T create(@NonNull Class<T> type){return (T)new CheckoutViewModel(CartRepositoryFactory.create(c),OrderRepositoryFactory.create(c));}}
