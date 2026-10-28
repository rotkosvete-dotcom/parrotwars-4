package foxgirl;

import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.NoteBlockEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ModEvents {
	@Mod.EventBusSubscriber(modid = FoxGirlMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ModBus {
		@SubscribeEvent
		public static void onAttributes(EntityAttributeCreationEvent event) {
			event.put(FoxGirlMod.FOX_GIRL.get(), FoxGirlEntity.createAttributes().build());
		}
	}

	@Mod.EventBusSubscriber(modid = FoxGirlMod.MOD_ID)
	public static class ForgeBus {
		/** Пластинка в проигрыватель — Фурритян слышит и идёт на звук (30 с). */
		@SubscribeEvent
		public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
			Level level = event.getLevel();
			if (level.isClientSide) return;
			BlockState st = level.getBlockState(event.getPos());
			ItemStack held = event.getItemStack();
			if (st.is(Blocks.JUKEBOX) && held.getItem() instanceof RecordItem && !st.getValue(JukeboxBlock.HAS_RECORD)) {
				Lures.add(level, event.getPos(), 600);
			}
		}

		/** Нотный блок — короткий шум (10 с). */
		@SubscribeEvent
		public static void onNote(NoteBlockEvent.Play event) {
			if (event.getLevel() instanceof Level level && !level.isClientSide()) {
				Lures.add(level, event.getPos(), 200);
			}
		}

		@SubscribeEvent
		public static void onServerStopping(ServerStoppingEvent event) { Lures.clear(); }

		/** /backrooms — телепорт туда и обратно (для операторов, удобно для проверки). */
		@SubscribeEvent
		public static void onCommands(RegisterCommandsEvent event) {
			event.getDispatcher().register(Commands.literal("backrooms")
				.requires(src -> src.hasPermission(2))
				.executes(ctx -> {
					ServerPlayer player = ctx.getSource().getPlayerOrException();
					Backrooms.toggle(player);
					return 1;
				}));
		}
	}
}
