package com.riintouge.tomeofperitia;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class EventHandlers implements Listener
{
    @EventHandler
    public void onPlayerInteract( PlayerInteractEvent event )
    {
        if( event.getMaterial() != Material.ENCHANTED_BOOK )
            return;
        else if( event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof TileState )
            return;

        ItemStack itemStack = event.getItem();
        if( itemStack == null || itemStack.getAmount() != 1 )
            return;

        ItemMeta itemMeta = itemStack.getItemMeta();
        if( !( itemMeta instanceof EnchantmentStorageMeta ) )
            return;
        else if( !( (EnchantmentStorageMeta)itemMeta ).hasStoredEnchant( Enchantment.MENDING ) )
            return;
        else if( !itemMeta.hasDisplayName() )
            return;

        TextComponent textComponent = (TextComponent)itemMeta.displayName();
        if( textComponent == null || !textComponent.content().equals( TomeOfPeritia.BookTitle ) )
            return;

        long storedExperience = 0;
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey( TomeOfPeritia.INSTANCE , TomeOfPeritia.StoredExperienceKey );
        if( !data.has( key , PersistentDataType.LONG ) )
            data.set( key , PersistentDataType.LONG , 0L );
        else
            storedExperience = data.get( key , PersistentDataType.LONG );

        Player player = event.getPlayer();
        int level = player.getLevel();
        long levelExperience = levelToExperience( level );
        int progressExperience = Math.round( player.getExp() * player.getExpToLevel() );

        if( player.isSneaking() )
        {
            if( event.getHand() == EquipmentSlot.OFF_HAND )
            {
                // Store all
                storedExperience += levelExperience + progressExperience;
                player.setLevel( 0 );
                player.setExp( 0.0f );
            }
            else if( player.getExp() == 0.0f )
            {
                // Store previous level
                storedExperience += levelExperience - levelToExperience( level - 1 );
                player.setLevel( Math.max( 0 , level - 1 ) );
            }
            else
            {
                // Store level progress
                storedExperience += progressExperience;
                player.setExp( 0.0f );
            }
        }
        else
        {
            if( event.getHand() == EquipmentSlot.OFF_HAND )
            {
                // Withdraw all
                storedExperience += levelExperience + progressExperience;
                int storedLevels = experienceToLevel( storedExperience );
                storedExperience -= levelToExperience( storedLevels );
                player.setLevel( storedLevels );
                player.setExp( 0.0f );
                player.giveExp( (int)storedExperience , false );
                storedExperience = 0;
            }
            else
            {
                // Withdraw to next level
                int experienceToLevel = Math.round( ( 1.0f - player.getExp() ) * player.getExpToLevel() );
                int exp = Math.min( (int)storedExperience , experienceToLevel );
                storedExperience -= exp;
                player.giveExp( exp , true );
            }
        }

        data.set( key , PersistentDataType.LONG , storedExperience );
        itemStack.setItemMeta( itemMeta );

        player.sendRawMessage(
            String.format(
                "Experience %s: %d (level %d)",
                player.isSneaking() ? "Stored" : "Remaining",
                storedExperience,
                experienceToLevel( storedExperience ) ) );
    }

    // Statics

    public static long levelToExperience( int level )
    {
        if( level <= 0 )
            return 0;
        else if( level <= 16 )
            return ( level * level ) + ( 6 * level );
        else if( level <= 30 )
            return Math.round( ( 2.5d * ( level * level ) ) - ( 40.5d * level ) + 360d );
        else
            return Math.round( ( 4.5d * ( (double)level * (double)level ) ) - ( 162.5d * (double)level ) + 2220d );
    }

    public static int experienceToLevel( long experience )
    {
        if( experience <= 0 )
            return 0;
        else if( experience <= 352 ) // Levels 0-16
            return (int)Math.floor( Math.sqrt( experience + 9 ) - 3.0d );
        else if( experience <= 1507 ) // Level 17-31
            return (int)Math.floor( ( ( 81.0d / 10.0d ) + Math.sqrt( ( 2.0d / 5.0d ) * ( (double)experience - ( 7839.0d / 40.0d ) ) ) ) );
        else // Levels 32+
        {
            // This math is accurate to approximately level 8222...
            int level = (int)Math.floor( ( ( 325.0d / 18.0d ) + Math.sqrt( ( 2.0d / 9.0d ) * ( (double)experience - ( 54215.0d / 72.0d ) ) ) ) );

            // ...at which point rounding errors creep in
            while( levelToExperience( level + 1 ) <= experience )
                level++;

            return level;
        }
    }
}
