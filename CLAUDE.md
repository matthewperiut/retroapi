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
├── RetroAPI.java                    # init entrypoint — fires registration events
│
├── client/                          # Client-side code
│   ├── RetroAPIClient.java          # client-init — ID sync + chunk extended receiver
│   ├── FurnaceBlockEntityProxy.java
│   ├── texture/
│   │   └── AtlasExpander.java       # Composites custom textures into terrain/item atlases
│   └── screen/
│       └── StationAPIWorldScreen.java  # Error screen for StationAPI worlds
│
├── server/
│   └── RetroAPIServer.java          # server-init — sends ID sync on player join
│
├── network/                         # Networking
│   ├── RetroAPINetworking.java      # Channel declarations (id_sync, chunk_ext)
│   ├── BlocksUpdatePacketAccess.java
│   └── WorldChunkPacketAccess.java
│
├── register/                        # Public API for mod authors
│   ├── RetroIdentifier.java         # record(namespace, path) — "mod:name"
│   ├── block/
│   │   ├── RetroBlockAccess.java    # Duck interface on Block (create, register, texture, bounds)
│   │   ├── BlockActivatedHandler.java
│   │   ├── RetroTexture.java        # Mutable sprite ID wrapper
│   │   ├── RetroTextures.java       # Texture registration and atlas tracking
│   │   └── event/
│   │       └── BlockRegistrationCallback.java
│   ├── item/
│   │   ├── RetroItemAccess.java     # Duck interface on Item
│   │   └── event/
│   │       └── ItemRegistrationCallback.java
│   ├── blockentity/
│   │   ├── RetroBlockEntityType.java
│   │   ├── SyncField.java
│   │   └── RetroMenu.java
│   └── rendertype/
│       ├── RenderType.java          # Custom block render type registry
│       ├── RenderTypes.java         # Vanilla render type constants
│       ├── CustomBlockRenderer.java
│       ├── BlockRenderContext.java
│       └── RetroBlockRendererAccess.java
│
├── registry/                        # ID management
│   ├── RetroRegistry.java
│   ├── BlockRegistration.java
│   ├── ItemRegistration.java
│   ├── IdAssigner.java
│   └── IdMap.java
│
├── storage/                         # Extended block storage (unlimited blocks beyond 256)
│   ├── ChunkExtendedBlocks.java
│   ├── ExtendedBlocksAccess.java
│   ├── RegionSidecar.java
│   ├── SidecarManager.java
│   ├── BackupManager.java
│   └── InventorySidecar.java
│
├── mixin/
│   ├── RetroAPIMixinPlugin.java     # Conditionally disables mixins when StationAPI present
│   ├── register/                    # Block/item/BE registration mixins
│   │   ├── BlockArrayExpandMixin.java
│   │   ├── BlockMixin.java
│   │   ├── ItemMixin.java
│   │   ├── ItemStackMixin.java
│   │   └── BlockEntityAccessor.java
│   ├── storage/                     # World/chunk persistence mixins
│   │   ├── AlphaWorldStorageMixin.java
│   │   ├── ChunkStorageMixin.java
│   │   ├── WorldChunkMixin.java
│   │   ├── WorldAccessor.java
│   │   ├── WorldStorageAccessor.java
│   │   └── RegionWorldStorageSourceAccessor.java
│   ├── network/                     # Packet serialization + server networking
│   │   ├── ChunkSendMixin.java
│   │   ├── BlockUpdatePacketMixin.java
│   │   ├── BlocksUpdatePacketMixin.java
│   │   ├── WorldChunkPacketMixin.java
│   │   └── ServerPlayerEntityAccessor.java
│   ├── client/                      # Client-side mixins
│   │   ├── BlockRendererMixin.java
│   │   ├── ClientNetworkHandlerMixin.java
│   │   ├── MinecraftMixin.java
│   │   ├── MinecraftAccessor.java
│   │   ├── PlayerRendererMixin.java
│   │   ├── LanguageAccessor.java
│   │   └── atlas/                   # Atlas/texture mixins (disabled with StationAPI)
│   │       ├── AchievementsScreenMixin.java
│   │       ├── BlockRendererAtlasMixin.java
│   │       ├── BlockParticleMixin.java
│   │       ├── ItemInHandRendererMixin.java
│   │       ├── ItemRendererMixin.java
│   │       └── TextureManagerMixin.java
│   └── stationapi/                  # StationAPI-specific mixins (separate config)
│       ├── FlattenedWorldStorageMixin.java
│       └── ModNioResourcePackMixin.java
│
├── compat/                          # StationAPI bridge
│   ├── StationAPICompat.java
│   ├── StationAPIRegistryForwarder.java
│   ├── WorldConversionHelper.java
│   └── WorldConversionProcessor.java
│
└── lang/
    └── LangLoader.java              # Loads translations from mod assets
```

## Key Architecture Concepts

### Block ID Expansion
Vanilla b1.7.3 uses `byte[]` for chunk block storage (max 256 IDs). RetroAPI:
1. Expands `Block.BY_ID` and related arrays from 256→4096 via `BlockArrayExpandMixin`
2. All RetroAPI blocks use IDs ≥ 256 (extended range). They are stored as `0` (air) in vanilla `byte[]`
3. `WorldChunkMixin` intercepts `getBlockAt`/`setBlockAt` to overlay real IDs from `ChunkExtendedBlocks`
4. Sidecar files (`retroapi/chunks/r.X.Z.dat`) persist extended blocks using **string identifiers** (not numeric IDs)

### ID Assignment Flow
1. Mods register blocks via `RetroBlockAccess.create(material).register(id)` during init. Placeholder IDs start at 256.
2. On world load (`AlphaWorldStorageMixin`), `IdAssigner.assignIds()` reads `retroapi/id_map.dat` and remaps blocks to stable numeric IDs
3. Stale entries in `id_map.dat` (blocks/items from removed mods) are purged automatically
4. `remapBlock()` takes `BlockRegistration` (not raw `Block`) so it uses the registration's stored `BlockItem` reference — this prevents BlockItem theft when IDs overlap during batch remapping
5. On multiplayer join, server sends ID mappings via `id_sync` channel; client remaps

### Vanilla Compatibility / Sidecar System

RetroAPI is designed so worlds can be opened in vanilla without crashing, and reopened in RetroAPI without data loss. All modded content is hidden from vanilla saves:

**Block Sidecar** (`retroapi/chunks/r.X.Z.dat`):
- Per-region files with string block identifiers for cross-mod stability
- Extended blocks (ID ≥ 256) are written as air in the vanilla byte array; real data lives in the sidecar
- Loaded on chunk load, saved on chunk save, flushed on world save

**Inventory Sidecar** (`retroapi/inventories/r.X.Z.dat`):
- Strips ALL modded content from vanilla chunk NBT before it reaches disk:
  - **Modded block entities** (e.g. CrateBlockEntity): stripped entirely from `TileEntities`, full NBT saved to sidecar
  - **RetroAPI items in vanilla inventories** (e.g. modded items in chests): stripped from `Items` lists, saved to sidecar
  - **Item entities** carrying RetroAPI items: stripped from `Entities`, saved to sidecar
- On load, restores everything from sidecar
- Block entity conflict handling: if vanilla placed a block entity at a modded position, the modded BE data is preserved in the sidecar (not overwritten) and re-checked each load

**ItemStack NBT** (`ItemStackMixin`):
- On write: saves `retroapi:id` (string identifier), `retroapi:count` (original count), `retroapi:damage` (original damage), then clamps `id=0, Count=0` so vanilla sees empty slots
- On read: if `retroapi:id` is present, resolves back to the correct numeric ID
- The clamped values exist only as a safety net for contexts not handled by the sidecar (e.g. player inventory). The sidecar reads original values from `retroapi:count`/`retroapi:damage`.

**Item entity restoration** creates `ItemEntity` directly via constructor (not NBT deserialization) to ensure proper pickup behavior. Position and motion are read from the saved entity NBT.

### Block Entities

RetroAPI blocks can have block entities without subclassing `BlockWithBlockEntity`:
- `RetroBlockEntityType<T>` registers the BE class and provides a factory
- `BlockMixin` injects into `onAdded`/`onRemoved` to create/remove BEs automatically
- `WorldChunkMixin` overrides `setBlockEntityAt`/`getBlockEntityAt` to bypass the vanilla `instanceof BlockWithBlockEntity` check for blocks with `HAS_BLOCK_ENTITY` flag
- Block activation (right-click) handled via `BlockActivatedHandler` callback set on the block

### Menus / Inventories

`RetroMenu.open(player, menu, menuType)` opens inventory GUIs:
- Uses `MENU_CHEST`, `MENU_FURNACE`, `MENU_DISPENSER` constants to select GUI type
- Client/server split: server sends vanilla open-window packet, client opens the appropriate GUI
- `@SyncField` annotation on menu fields enables automatic slot synchronization

### Translation / Lang

- `LangLoader` loads translations from `assets/{modid}/retroapi/lang/en_US.lang`
- Auto-generates default translations for any block/item without one (e.g. `test_block` → `Test Block`)
- Works with both StationAPI and non-StationAPI (injects into `Language.translations` properties)

### Multiplayer Sync
- `ChunkSendMixin` hooks `ServerPlayNetworkHandler.sendPacket` — when a `WorldChunkPacket` is sent, it also sends extended block data via `chunk_ext` channel
- Client receives and populates `ChunkExtendedBlocks`, then triggers re-render

## Test Mod

Located in `src/test/`. Registers:
- 5 special blocks (test_block, color_block, pipe, crate with inventory, freezer with furnace-style menu)
- 200 numbered blocks (block_0 through block_199) — spawned in chests near the player
- 200 numbered items (item_0 through item_199) — spawned in a second row of chests
- 1 test item

Mixin config: `retroapi_test.mixins.json`. Run with `./gradlew runClient`.

## Access Widener

`src/main/resources/retroapi.accesswidener` — makes Block/Item fields mutable, Block constructor accessible, and all 8 Block static arrays (BY_ID, IS_SOLID_RENDER, OPACITIES, etc.) accessible + mutable for runtime expansion. Also exposes BlockItem.block field.

## Important Implementation Notes

- **All RetroAPI block IDs are ≥ 256.** `allocatePlaceholderBlockId()` and `findFreeBlockId()` both start from 256. This keeps modded blocks out of vanilla's 0-255 byte range entirely.
- **`remapBlock()` must use `BlockRegistration`'s stored BlockItem reference**, not blindly grab from `Item.BY_ID[oldId]`. During batch remapping, IDs can overlap (block A remapped TO id X, then block B remapped FROM id X), which causes BlockItem theft if not using the stored reference.
- **`NbtCompound` has no key iteration API** in b1.7.3. The `getKeys()` helper in `IdMap` and `InventorySidecar` serializes to bytes and re-reads the binary NBT format to extract keys.
- **`ItemStack.metadata` is private.** Use `getMetadata()` accessor or `@Shadow` in mixins.
- **`NbtDouble.value`** (not `.data`) for reading double values from NBT lists.
- **Item entities must be created via constructor**, not `Entities.create(nbt, world)`, to ensure proper pickup behavior and correct stack data.

## StationAPI Compatibility

When StationAPI is present:
- Registration delegates to StationAPI's registry
- Atlas mixins are disabled (StationAPI handles textures)
- `IdAssigner.saveCurrentIds()` is called instead of `assignIds()` (StationAPI manages IDs)
- `BackupManager.backupRetroApiData()` runs before world format conversions
- `WorldConversionHelper` injects RetroAPI mappings into StationAPI's flattening schema
- Lang defaults are still injected (works for both StationAPI and non-StationAPI)
