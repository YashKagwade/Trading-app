package com.trading.entities;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Coin {
//You only need @JsonProperty when names don't match.
	@Id
	@JsonProperty("id")
	private String id;
	@JsonProperty("symbol")
	private String symbol;
	@JsonProperty("name")
	private String name;
	@JsonProperty("image")
	private String image;
	@JsonProperty("current_price")
	private double currentPrice;
	
	@JsonProperty("market_cap")
	private long marketCap;

	@JsonProperty("market_cap_rank")
	private int marketCapRank;

	@JsonProperty("fully_diluted_valuation")
	private long fullyDilutedValuation;

	@JsonProperty("total_volume")
	private long totalVolume;

	@JsonProperty("high_24h")
	private double high24h;

	@JsonProperty("low_24h")
	private double low24h;
	
	@JsonProperty("price_change_24h")
	private double priceChange24h;

	@JsonProperty("price_change_percentage_24h")
	private double priceChangePercentage24h;

	@JsonProperty("market_cap_change_24h")
	private long marketCapChange24h;

	@JsonProperty("market_cap_change_percentage_24h")
	private double marketCapChangePercentage24h;

	@JsonProperty("circulating_supply")
	private long circulatingSupply;
	
	@JsonProperty("total_supply")
	private long totalSupply;

	@JsonProperty("max_supply")
	private long maxSupply;

	@JsonProperty("ath")
	private double ath;
	

    @JsonProperty("ath_date")
    private Date athDate;

    @JsonProperty("atl")
    private double atl;

    @JsonProperty("atl_change_percentage")
    private double atlChangePercentage;

    @JsonProperty("atl_date")
    private Date atlDate;

    @JsonProperty("roi")
    @JsonIgnore
    private String roi;

    @JsonProperty("last_updated")
    private Date lastUpdated;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public long getMarketCap() {
		return marketCap;
	}

	public void setMarketCap(long marketCap) {
		this.marketCap = marketCap;
	}

	public int getMarketCapRank() {
		return marketCapRank;
	}

	public void setMarketCapRank(int marketCapRank) {
		this.marketCapRank = marketCapRank;
	}

	public long getFullyDilutedValuation() {
		return fullyDilutedValuation;
	}

	public void setFullyDilutedValuation(long fullyDilutedValuation) {
		this.fullyDilutedValuation = fullyDilutedValuation;
	}

	public long getTotalVolume() {
		return totalVolume;
	}

	public void setTotalVolume(long totalVolume) {
		this.totalVolume = totalVolume;
	}

	public double getHigh24h() {
		return high24h;
	}

	public void setHigh24h(double high24h) {
		this.high24h = high24h;
	}

	public double getLow24h() {
		return low24h;
	}

	public void setLow24h(double low24h) {
		this.low24h = low24h;
	}

	public double getPriceChange24h() {
		return priceChange24h;
	}

	public void setPriceChange24h(double priceChange24h) {
		this.priceChange24h = priceChange24h;
	}

	public double getPriceChangePercentage24h() {
		return priceChangePercentage24h;
	}

	public void setPriceChangePercentage24h(double priceChangePercentage24h) {
		this.priceChangePercentage24h = priceChangePercentage24h;
	}

	public long getMarketCapChange24h() {
		return marketCapChange24h;
	}

	public void setMarketCapChange24h(long marketCapChange24h) {
		this.marketCapChange24h = marketCapChange24h;
	}

	public double getMarketCapChangePercentage24h() {
		return marketCapChangePercentage24h;
	}

	public void setMarketCapChangePercentage24h(double marketCapChangePercentage24h) {
		this.marketCapChangePercentage24h = marketCapChangePercentage24h;
	}

	public long getCirculatingSupply() {
		return circulatingSupply;
	}

	public void setCirculatingSupply(long circulatingSupply) {
		this.circulatingSupply = circulatingSupply;
	}

	public long getTotalSupply() {
		return totalSupply;
	}

	public void setTotalSupply(long totalSupply) {
		this.totalSupply = totalSupply;
	}

	public long getMaxSupply() {
		return maxSupply;
	}

	public void setMaxSupply(long maxSupply) {
		this.maxSupply = maxSupply;
	}

	public double getAth() {
		return ath;
	}

	public void setAth(double ath) {
		this.ath = ath;
	}

	public Date getAthDate() {
		return athDate;
	}

	public void setAthDate(Date athDate) {
		this.athDate = athDate;
	}

	public double getAtl() {
		return atl;
	}

	public void setAtl(double atl) {
		this.atl = atl;
	}

	public double getAtlChangePercentage() {
		return atlChangePercentage;
	}

	public void setAtlChangePercentage(double atlChangePercentage) {
		this.atlChangePercentage = atlChangePercentage;
	}

	public Date getAtlDate() {
		return atlDate;
	}

	public void setAtlDate(Date atlDate) {
		this.atlDate = atlDate;
	}

	public String getRoi() {
		return roi;
	}

	public void setRoi(String roi) {
		this.roi = roi;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
}
