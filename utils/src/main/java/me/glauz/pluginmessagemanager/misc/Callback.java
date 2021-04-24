package me.glauz.pluginmessagemanager.misc;

public interface Callback<T, U> {
    T call(U... args);
}
