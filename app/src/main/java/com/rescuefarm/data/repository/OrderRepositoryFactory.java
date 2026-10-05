package com.rescuefarm.data.repository;
import android.content.Context;
import com.rescuefarm.data.remote.firebase.FirebaseOrderRepository;
import com.rescuefarm.service.network.AndroidNetworkStatusProvider;
public final class OrderRepositoryFactory { private OrderRepositoryFactory(){} public static OrderRepository create(Context context){return new FirebaseOrderRepository(new AndroidNetworkStatusProvider(context));} }
