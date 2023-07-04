package com.lzb.sie;

import com.sie.snest.engine.container.AppLifecycleProcess;
import com.sie.snest.engine.container.load.WebMode;
import com.sie.snest.engine.container.load.factory.LoaderFactory;
import com.sie.snest.engine.container.manger.IMetaManager;
import com.sie.snest.engine.container.manger.IMetaType;
import com.sie.snest.engine.container.manger.factory.IMetaFactory;
import com.sie.snest.engine.model.Loader;
import com.sie.snest.engine.pubsub.*;
import com.sie.snest.engine.utils.ConfigUtils;

public class Engine {


    public static void main(String[] args) throws InterruptedException {
        ConfigUtils.loadAllConfig();

        String metaStoreType = ConfigUtils.get("engine.store.meta.mode");
        String runMode = ConfigUtils.get("engine.run.mode");

        AppLifecycleProcess loader = LoaderFactory.getAppLoader(WebMode.of(runMode));
        IMetaManager metaManager = IMetaFactory.getMetaStoreManager(IMetaType.fromString(metaStoreType));
        loader.setMetaManager(metaManager);
        // 启动引擎
        Loader.setLoader(loader);
        // logger.info("Loader: {}, IMetaManager: {}", loader.getClass().getName(), metaManager.getClass().getName());

        com.sie.snest.engine.container.Engine.start();
        
        RedisStream rs = RedisStream.startStream();

        Thread.currentThread().sleep(999999);
    }
}
