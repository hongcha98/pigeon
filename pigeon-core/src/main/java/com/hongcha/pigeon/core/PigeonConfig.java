package com.hongcha.pigeon.core;

import com.hongcha.pigeon.core.registry.RegistryConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PigeonConfig {
    private int port = 30800;

    private String[] packages;

    private RegistryConfig registry;

}
