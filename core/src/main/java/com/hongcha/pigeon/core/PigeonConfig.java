package com.hongcha.pigeon.core;


import com.hongcha.pigeon.registry.RegistryConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PigeonConfig {
    private int port = 30800;

    private String[] packages = new String[]{};

    private RegistryConfig registry;

}
