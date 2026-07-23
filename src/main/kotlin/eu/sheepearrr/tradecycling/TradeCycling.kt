package eu.sheepearrr.tradecycling

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.npc.villager.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MerchantContainer
import net.minecraft.world.inventory.MerchantMenu
import net.minecraft.world.item.trading.Merchant
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.Field
import java.lang.reflect.Method

class TradeCycling : JavaPlugin(), Listener {
    companion object {
        const val VERSION = "1.0+1.21.11"
        val PAYLOAD = Identifier.fromNamespaceAndPath("trade_cycling", "cycle_trades")
        // Reflection
        val TRADER_FIELD: Field = MerchantMenu::class.java.getDeclaredField("trader").apply { this.isAccessible = true }
        val TRADE_CONTAINER_FIELD: Field = MerchantMenu::class.java.getDeclaredField("tradeContainer").apply { this.isAccessible = true }
        val UPDATE_SPECIAL_PRICES_METHOD: Method = Villager::class.java.getDeclaredMethod("updateSpecialPrices", Player::class.java).apply { this.isAccessible = true }

        lateinit var INSTANCE: TradeCycling
            private set
    }

    override fun onEnable() {
        INSTANCE = this
        this.componentLogger.info(MiniMessage.miniMessage().deserialize("Starting Trade Cycling v$VERSION"))
        this.server.pluginManager.registerEvents(this, this)
    }

    @EventHandler
    fun attachNetworkListener(event: PlayerJoinEvent) {
        val player = (event.player as CraftPlayer).handle
        player.connection.connection.channel.pipeline().addBefore("packet_handler", "trade_cycling_${event.player.name}", object : ChannelDuplexHandler() {
            override fun channelRead(context: ChannelHandlerContext?, packet: Any?) {
                if (packet is ServerboundCustomPayloadPacket && packet.payload.type().id == PAYLOAD && player.containerMenu is MerchantMenu) {
                    Bukkit.getScheduler().runTask(INSTANCE, Runnable {
                        val container = player.containerMenu as MerchantMenu
                        val merchant = TRADER_FIELD.get(container) as Merchant
                        if (container.traderXp > 0 && (TRADE_CONTAINER_FIELD.get(container) as MerchantContainer).activeOffer != null || merchant !is Villager || merchant.brain.getMemory(MemoryModuleType.JOB_SITE).isEmpty)
                            return@Runnable
                        merchant.resetOffers()
                        UPDATE_SPECIAL_PRICES_METHOD.invoke(merchant, player)
                        merchant.tradingPlayer = player
                        player.sendMerchantOffers(container.containerId, merchant.offers, merchant.villagerData.level, merchant.villagerXp, merchant.showProgressBar(), merchant.canRestock())
                    })
                    return
                }
                super.channelRead(context, packet)
            }
        })
    }

    @EventHandler
    fun detachNetworkListener(event: PlayerQuitEvent) {
        val channel = (event.player as CraftPlayer).handle.connection.connection.channel
        val handler = "trade_cycling_${event.player.name}"
        channel.eventLoop().execute {
            if (channel.pipeline().get(handler) != null)
                channel.pipeline().remove(handler)
        }
    }
}
