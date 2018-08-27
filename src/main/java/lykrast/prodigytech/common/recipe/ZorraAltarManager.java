package lykrast.prodigytech.common.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lykrast.prodigytech.common.util.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ZorraAltarManager {
	public static final ZorraAltarManager SWORD = new ZorraAltarManager();
	
	public static void init() {
		//------------------------
		//Sword
		//------------------------
		SWORD.addEnchant(Enchantments.SHARPNESS, 8);
		SWORD.addEnchant(Enchantments.SMITE, 8);
		SWORD.addEnchant(Enchantments.BANE_OF_ARTHROPODS, 8);
		SWORD.addEnchant(Enchantments.FIRE_ASPECT, 5);
		SWORD.addEnchant(Enchantments.KNOCKBACK, 5);
		SWORD.addEnchant(Enchantments.LOOTING, 6);
		SWORD.addEnchant(Enchantments.SWEEPING, 6);

		ItemStack checker = new ItemStack(Items.IRON_SWORD);
		//We only want 1 Soulbound
		boolean hasSoulbound = false;

		//EnderCore
		if (Loader.isModLoaded("endercore")) SWORD.addModdedEnchant("endercore:xpboost", 6, checker);
		
		//Ender IO
		if (Loader.isModLoaded("enderio"))
		{
			SWORD.addModdedEnchant("enderio:witherweapon", 1, checker);
			if (!hasSoulbound) {
				if (SWORD.addModdedEnchant("enderio:soulbound", 1, checker)) hasSoulbound = true;
			}
		}
		
		//CoFH Core
		if (Loader.isModLoaded("cofhcore"))
		{
			//CoFH Core makes enchants unapplicable and useless instead of not registering them when disabled individually
			//So we use this dummy ItemStack to check if it's applicable
			SWORD.addModdedEnchant("cofhcore:insight", 6, checker);
			SWORD.addModdedEnchant("cofhcore:leech", 7, checker);
			SWORD.addModdedEnchant("cofhcore:vorpal", 6, checker);

			Enchantment soulbound = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation("cofhcore:soulbound"));
			if (!hasSoulbound && soulbound != null && soulbound.canApply(checker))
			{
				hasSoulbound = true;
				//Check if it was configured to be permanent
				if (soulbound.getMaxLevel() == 1) SWORD.addEnchant(soulbound, 1);
				else SWORD.addEnchant(soulbound, 6);
			}
		}
		
		//Cyclic
		if (Loader.isModLoaded("cyclicmagic"))
		{
			SWORD.addModdedEnchant("cyclicmagic:enchantment.beheading", 1, checker);
			SWORD.addModdedEnchant("cyclicmagic:enchantment.lifeleech", 5, checker);
			SWORD.addModdedEnchant("cyclicmagic:enchantment.venom", 5, checker);
		}
		
		//Draconic Evolution
		if (Loader.isModLoaded("draconicevolution")) SWORD.addModdedEnchant("draconicevolution:enchant_reaper", 8, checker);
		
		//AbyssalCraft
		if (Loader.isModLoaded("abyssalcraft")) SWORD.addModdedEnchant("abyssalcraft:light_pierce", 8, checker);
		
		//Soul Shards Respawn
		if (Loader.isModLoaded("soulshardsrespawn")) SWORD.addModdedEnchant("soulshardsrespawn:soul_stealer", 8, checker);
		
		//EvilCraft
		if (Loader.isModLoaded("evilcraft")) SWORD.addModdedEnchant("evilcraft:life_stealing", 6, checker);
		
	}
	
	private List<EnchantmentData> enchants;
	
	public ZorraAltarManager() {
		enchants = new ArrayList<>();
	}
	
	public void addEnchant(EnchantmentData enchant) {
		enchants.add(enchant);
	}
	
	/**
	 * Adds the given enchantment to the pool, with the given maximum level
	 * @param enchant enchantment to add
	 * @param maxLvl maximum applicable level
	 */
	public void addEnchant(Enchantment enchant, int maxLvl) {
		enchants.add(new EnchantmentData(enchant, maxLvl));
	}
	
	/**
	 * Attempts to add the given enchantment given its registry name
	 * @param key registry name of the enchantment to add
	 * @param maxLvl maximum applicable level
	 * @param checker ItemStack that should be enchantable with the target enchantment, used to check if the enchantment is disabled
	 * @return true if the enchant was found, could be applied and has been added, false otherwise
	 */
	public boolean addModdedEnchant(String key, int maxLvl, ItemStack checker) {
		Enchantment enchant = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(key));
		//CoFH Core makes enchantments unapplicable and useless instead of not registering them when disabled individually
		//So we use this dummy ItemStack to check if it's applicable
		if (enchant == null || !enchant.canApply(checker)) return false;
		
		addEnchant(enchant, maxLvl);
		return true;
	}
	
	/**
	 * Gives the base cost in levels of applying this enchantment
	 * @param data EnchantmentData to apply
	 * @return cost in level to apply it
	 */
	public int getLevelCost(EnchantmentData data) {
		int lvl = data.enchantmentLevel, cost;
		if (lvl <= 1) cost = data.enchantment.getMinEnchantability(1);
		else cost = data.enchantment.getMinEnchantability(lvl) - (data.enchantment.getMinEnchantability(lvl - 1) / 2);
		return Math.max(1, (int)(cost * Config.altarCostMult));
	}
	
	/**
	 * Applies a random deviation to the given level cost
	 * @param cost level cost to deviate
	 * @param rand Random to use
	 * @return the cost randomly deviated
	 */
	public int deviate(int cost, Random rand) {
		int deviation = Math.max(Config.altarDeviationMin, (int)(cost * Config.altarDeviationMult));
		return Math.max(1, cost - deviation + rand.nextInt(deviation * 2 + 1));
	}
	
	/**
	 * Gives a deviated level cost for applying the given enchantment
	 * @param data EnchantmentData to apply
	 * @param rand Random to use
	 * @return cost in level to apply it, randomly deviated
	 */
	public int getRandomLevelCost(EnchantmentData data, Random rand) {
		return deviate(getLevelCost(data), rand);
	}
	
	public List<EnchantmentData> getAvailableEnchants(ItemStack stack) {
		List<EnchantmentData> list = new ArrayList<>();
		Map<Enchantment,Integer> map = EnchantmentHelper.getEnchantments(stack);
		for (EnchantmentData data : enchants) {
			Integer lvl = map.get(data.enchantment);
			if (lvl == null) list.add(new EnchantmentData(data.enchantment, 1));
			else if (lvl < data.enchantmentLevel) list.add(new EnchantmentData(data.enchantment, lvl + 1));
		}
		
		return list;
	}
	
	/**
	 * Gives 3 random enchantments for the Zorra Altar
	 * @param stack ItemStack to apply enchantments for
	 * @param rand Random to use
	 * @return up to 3 applicable enchantments
	 */
	public EnchantmentData[] getRandomEnchants(ItemStack stack, Random rand) {
		List<EnchantmentData> apply = new ArrayList<>();
		List<EnchantmentData> upgrade = new ArrayList<>();
		
		Map<Enchantment,Integer> map = EnchantmentHelper.getEnchantments(stack);
		for (EnchantmentData data : enchants) {
			Integer lvl = map.get(data.enchantment);
			if (lvl == null) apply.add(new EnchantmentData(data.enchantment, 1));
			else if (lvl < data.enchantmentLevel) upgrade.add(new EnchantmentData(data.enchantment, lvl + 1));
		}
		
		EnchantmentData[] datas = new EnchantmentData[3];
		
		if (!apply.isEmpty() || !upgrade.isEmpty())
		{
			//1st is always a new enchant if possible
			if (!apply.isEmpty()) {
				int i = rand.nextInt(apply.size());
				datas[0] = apply.remove(i);
			}
			else {
				int i = rand.nextInt(upgrade.size());
				datas[0] = upgrade.remove(i);
			}
			
			//2nd is always an upgrade if possible
			if (!upgrade.isEmpty()) {
				int i = rand.nextInt(upgrade.size());
				datas[1] = upgrade.remove(i);
			}
			else if (!apply.isEmpty()) {
				int i = rand.nextInt(apply.size());
				datas[1] = apply.remove(i);
			}
			
			//3rd is random
			apply.addAll(upgrade);
			if (!apply.isEmpty()) {
				int i = rand.nextInt(apply.size());
				datas[2] = apply.remove(i);
			}
		}
			
		return datas;
	}
}