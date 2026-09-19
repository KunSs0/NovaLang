package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

/** CreatureSpawner 方块状态在 Spigot 1.12.2 可用的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.block.CreatureSpawner"})
final class NovaCreatureSpawner {

    private NovaCreatureSpawner() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(CreatureSpawner.class, "spawnedType", function -> function.returns(EntityType.class)
                .invoke(arguments -> spawner(arguments).getSpawnedType()));
        builder.extension(CreatureSpawner.class, "setSpawnedType", function -> function.param("type", EntityType.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    spawner(arguments).setSpawnedType(NovaTypeSupport.argument(arguments, 1, EntityType.class));
                    return null;
                }));
        builder.extension(CreatureSpawner.class, "setSpawnedType", function -> function.param("type", String.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    EntityType type = NovaTypeSupport.findEnum(EntityType.class,
                            NovaTypeSupport.argument(arguments, 1, String.class));
                    if (type != null) {
                        spawner(arguments).setSpawnedType(type);
                    }
                    return null;
                }));
        builder.extension(CreatureSpawner.class, "setCreatureTypeByName", function -> function.param("name", String.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    spawner(arguments).setCreatureTypeByName(NovaTypeSupport.argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(CreatureSpawner.class, "creatureTypeName", function -> function.returns(String.class)
                .invoke(arguments -> spawner(arguments).getCreatureTypeName()));
        registerInteger(builder, "delay", "setDelay", new Getter() {
            @Override
            public int get(CreatureSpawner spawner) {
                return spawner.getDelay();
            }
        }, new Setter() {
            @Override
            public void set(CreatureSpawner spawner, int value) {
                spawner.setDelay(value);
            }
        });
        registerInteger(builder, "minSpawnDelay", "setMinSpawnDelay", new Getter() {
            @Override
            public int get(CreatureSpawner spawner) {
                return spawner.getMinSpawnDelay();
            }
        }, new Setter() {
            @Override
            public void set(CreatureSpawner spawner, int value) {
                spawner.setMinSpawnDelay(value);
            }
        });
        registerInteger(builder, "maxSpawnDelay", "setMaxSpawnDelay", new Getter() {
            @Override
            public int get(CreatureSpawner spawner) {
                return spawner.getMaxSpawnDelay();
            }
        }, new Setter() {
            @Override
            public void set(CreatureSpawner spawner, int value) {
                spawner.setMaxSpawnDelay(value);
            }
        });
        registerInteger(builder, "spawnCount", "setSpawnCount", new Getter() {
            @Override
            public int get(CreatureSpawner spawner) {
                return spawner.getSpawnCount();
            }
        }, new Setter() {
            @Override
            public void set(CreatureSpawner spawner, int value) {
                spawner.setSpawnCount(value);
            }
        });
        registerInteger(builder, "maxNearbyEntities", "setMaxNearbyEntities", new Getter() {
            @Override
            public int get(CreatureSpawner spawner) {
                return spawner.getMaxNearbyEntities();
            }
        }, new Setter() {
            @Override
            public void set(CreatureSpawner spawner, int value) {
                spawner.setMaxNearbyEntities(value);
            }
        });
        registerInteger(builder, "requiredPlayerRange", "setRequiredPlayerRange", new Getter() {
            @Override
            public int get(CreatureSpawner spawner) {
                return spawner.getRequiredPlayerRange();
            }
        }, new Setter() {
            @Override
            public void set(CreatureSpawner spawner, int value) {
                spawner.setRequiredPlayerRange(value);
            }
        });
        registerInteger(builder, "spawnRange", "setSpawnRange", new Getter() {
            @Override
            public int get(CreatureSpawner spawner) {
                return spawner.getSpawnRange();
            }
        }, new Setter() {
            @Override
            public void set(CreatureSpawner spawner, int value) {
                spawner.setSpawnRange(value);
            }
        });
    }

    private static void registerInteger(JavaTypes.Builder builder, String getterName, String setterName,
                                        Getter getter, Setter setter) {
        builder.extension(CreatureSpawner.class, getterName, function -> function.returns(Integer.class)
                .invoke(arguments -> getter.get(spawner(arguments))));
        builder.extension(CreatureSpawner.class, setterName, function -> function.param("value", Integer.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    setter.set(spawner(arguments), NovaTypeSupport.argument(arguments, 1, Integer.class));
                    return null;
                }));
    }

    private static CreatureSpawner spawner(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, CreatureSpawner.class);
    }

    private interface Getter {
        int get(CreatureSpawner spawner);
    }

    private interface Setter {
        void set(CreatureSpawner spawner, int value);
    }
}
