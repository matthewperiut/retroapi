# RetroAPI - AI Agent Onboarding

## What This Is

RetroAPI is a Fabric mod for **Minecraft Beta 1.7.3** that lets other mods register custom blocks and items without requiring StationAPI. It handles ID assignment, texture atlasing, custom rendering, networking, and world persistence. When StationAPI is present, RetroAPI delegates to it instead.

## Build & Run

```bash
./gradlew build          # Compiles main + test mod, outputs to build/libs/
./gradlew runClient      # Launches client with test mod loaded
./gradlew runServer      # Launches dedicated server with test mod loaded
```

Java 21 required. Uses Fabric Loom + Ploceus (Feather mappings for b1.7.3).

## Mapping Gotchas (CRITICAL)

This project uses **Feather build 1** mappings (hash `845945349`), NOT modern Yarn/Mojmap. Many class/field/method names differ from what you'd expect:

| What you'd expect | Actual name in this project |
|---|---|
| `Chunk` | `WorldChunk` |
| `Chunk.x` / `Chunk.z` | `WorldChunk.chunkX` / `WorldChunk.chunkZ` |
| `Chunk.getBlockId()` | `WorldChunk.getBlockAt()` |
| `Chunk.setBlock(x,y,z,id)` | `WorldChunk.setBlockAt(x,y,z,id)` |
| `World.getChunk(x,z)` | `World.getChunkAt(x,z)` |
| `World.isRemote` / `isClient` | `World.isMultiplayer` |
| `WorldStorage.save()` | `WorldStorage.saveData(WorldData, List)` |
| `ItemStack.itemId` / `count` | `ItemStack.id` / `size` |
| `NbtCompound.containsKey()` | `NbtCompound.contains()` |
| `ChunkDataS2CPacket` | `WorldChunkPacket` |
| `ServerPlayNetworkHandler` | `net.minecraft.server.network.handler.ServerPlayNetworkHandler` |
| `ServerPlayerEntity` | `net.minecraft.server.entity.mob.player.ServerPlayerEntity` |

**NbtCompound has NO `getIntArray`/`putIntArray`** in b1.7.3. Use `putByteArray`/`getByteArray` with manual int↔byte conversion.

**Static methods matter for mixins.** `AlphaChunkStorage.loadChunkFromNbt` and `saveChunkToNbt` are static — mixin callbacks targeting them must also be `private static`.

To look up mappings yourself, the file is at:
`~/.gradle/caches/fabric-loom/b1.7.3/loom.mappings.b1_7_3.layered+hash.845945349-v2/mappings.tiny`

The merged MC jar with all named classes is at:
`~/.gradle/caches/fabric-loom/b1.7.3/loom.mappings.b1_7_3.layered+hash.845945349-v2/merged-unpicked.jar`
Use `jar tf` on it to verify class/package names before writing imports.

## OSL Networking API

- Client context: `ctx.minecraft()` (not `getMinecraft()`)
- Client listener: `ClientPlayNetworking.registerListener(channel, (ctx, buffer) -> { ... })`
- Server send: `ServerPlayNetworking.send(player, channel, writerLambda)`
- Channels registered via `ChannelRegistry.register(ChannelIdentifiers.from("retroapi", "name"), serverToClient, clientToServer)`

## Package Structure

```
com.periut.retroapi/
├── RetroAPI.java              # init entrypoint — fires registration events
├── RetroAPIClient.java        # client-init — ID sync + chunk extended receiver
├── RetroAPIServer.java        # server-init — sends ID sync on player join
├── RetroAPINetworking.java    # Channel declarations (id_sync, chunk_ext)
│
├── api/                       # Public API for mod authors
│   ├── RetroBlockAccess.java  # Duck interface on Block (create, register, texture, bounds)
│   ├── RetroItemAccess.java   # Duck interface on Item
│   ├── RetroIdentifier.java   # record(namespace, path) — "mod:name"
│   ├── RetroTexture.java      # Mutable sprite ID wrapper
│   ├── RetroTextures.java     # Texture registration and atlas tracking
│   ├── RenderType.java        # Custom block render type registry
│   ├── RenderTypes.java       # Vanilla render type constants
│   ├── CustomBlockRenderer.java
│   ├── BlockRenderContext.java # Context for custom renderers (faces, AO, lighting)
│   └── event/                 # Registration callbacks
│
├── registry/                  # ID management
│   ├── RetroRegistry.java     # Central list of BlockRegistration/ItemRegistration
│   ├── BlockRegistration.java # Holds block + blockItem + RetroIdentifier
│   ├── ItemRegistration.java  # Holds item + RetroIdentifier
│   ├── IdAssigner.java        # Assigns/remaps numeric IDs, grows arrays if needed
│   └── IdMap.java             # NBT persistence for id_map.dat
│
├── storage/                   # Extended block storage (unlimited blocks beyond 256)
│   ├── ChunkExtendedBlocks.java   # Sparse HashMap<index, blockId> per chunk
│   ├── ExtendedBlocksAccess.java  # Duck interface on WorldChunk
│   ├── RegionSidecar.java         # Per-region sidecar file (retroapi/chunks/r.X.Z.dat)
│   ├── SidecarManager.java        # Cached region sidecar access
│   └── BackupManager.java         # Date-stamped backups before conversions
│
├── mixin/                     # All Sponge mixins
│   ├── BlockArrayExpandMixin.java  # Expands Block.BY_ID etc from 256→4096 at clinit
│   ├── BlockMixin.java             # Injects RetroBlockAccess onto Block
│   ├── ItemMixin.java              # Injects RetroItemAccess onto Item
│   ├── WorldChunkMixin.java        # Overlays get/setBlock with extended storage
│   ├── ChunkStorageMixin.java      # Hooks AlphaChunkStorage save/load for sidecars
│   ├── ChunkSendMixin.java         # Hooks ServerPlayNetworkHandler.sendPacket for chunk sync
│   ├── ItemStackMixin.java         # Embeds string IDs in ItemStack NBT
│   ├── AlphaWorldStorageMixin.java # Inits SidecarManager + ID assignment on world load
│   ├── BlockRendererMixin.java     # Custom render types + smooth lighting
│   ├── TextureManagerMixin.java    # Atlas expansion (non-StationAPI)
│   ├── MinecraftMixin.java         # StationAPI world detection
│   ├── RetroAPIMixinPlugin.java    # Conditionally disables atlas mixins when StationAPI present
│   └── stationapi/                 # StationAPI-specific mixins (separate config)
│
├── compat/                    # StationAPI bridge
│   ├── StationAPICompat.java
│   ├── StationAPIRegistryForwarder.java
│   └── WorldConversionHelper.java
│
├── texture/
│   └── AtlasExpander.java     # Composites custom textures into terrain/item atlases
│
├── lang/
│   └── LangLoader.java        # Loads translations from mod assets
│
└── screen/
    └── StationAPIWorldScreen.java  # Error screen for StationAPI worlds
```

## Key Architecture Concepts

### Block ID Expansion
Vanilla b1.7.3 uses `byte[]` for chunk block storage (max 256 IDs). RetroAPI:
1. Expands `Block.BY_ID` and related arrays from 256→4096 via `BlockArrayExpandMixin`
2. Blocks with ID ≥ 256 are stored as `0` (air) in vanilla `byte[]`
3. `WorldChunkMixin` intercepts `getBlockAt`/`setBlockAt` to overlay real IDs from `ChunkExtendedBlocks`
4. Sidecar files (`retroapi/chunks/r.X.Z.dat`) persist extended blocks using **string identifiers** (not numeric IDs)

### ID Assignment Flow
1. Mods register blocks via `RetroBlockAccess.create(material).register(id)` during init
2. On world load (`AlphaWorldStorageMixin`), `IdAssigner.assignIds()` reads `retroapi/id_map.dat` and remaps blocks to stable numeric IDs
3. On multiplayer join, server sends ID mappings via `id_sync` channel; client remaps

### Sidecar Storage
- Per-region files at `worldDir/retroapi/chunks/r.{regionX}.{regionZ}.dat`
- NBT format with string block identifiers for cross-mod stability
- Loaded on chunk load, saved on chunk save, flushed on world save
- Without RetroAPI installed, extended blocks appear as air (vanilla compatible)

### Multiplayer Sync
- `ChunkSendMixin` hooks `ServerPlayNetworkHandler.sendPacket` — when a `WorldChunkPacket` is sent, it also sends extended block data via `chunk_ext` channel
- Client receives and populates `ChunkExtendedBlocks`, then triggers re-render

## Test Mod

Located in `src/test/`. Registers 250+ blocks and spawns chests filled with them at the player. Mixin config: `retroapi_test.mixins.json`. Run with `./gradlew runClient`.

## Access Widener

`src/main/resources/retroapi.accesswidener` — makes Block/Item fields mutable, Block constructor accessible, and all 8 Block static arrays (BY_ID, IS_SOLID_RENDER, OPACITIES, etc.) accessible + mutable for runtime expansion.

## StationAPI Compatibility

When StationAPI is present:
- Registration delegates to StationAPI's registry
- Atlas mixins are disabled (StationAPI handles textures)
- `IdAssigner.saveCurrentIds()` is called instead of `assignIds()` (StationAPI manages IDs)
- `BackupManager.backupRetroApiData()` runs before world format conversions
- `WorldConversionHelper` injects RetroAPI mappings into StationAPI's flattening schema
