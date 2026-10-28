package live.crowdcontrol.cc4j.websocket.http;

import live.crowdcontrol.cc4j.websocket.payload.CCName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Builder for {@link CustomEffect}.
 * <p>
 * Use {@link #builder()} to obtain a staged builder which enforces at compile time
 * that the required {@code name} and {@code price} are set before any optional components.
 */
public class CustomEffectBuilder {
	private @NotNull CCName name;
	private int price;
	private @Nullable String description;
	private @Nullable List<String> category;
	private @Nullable String image;
	private @Nullable CustomEffectDuration duration;
	private @Nullable CustomEffectQuantity quantity;
	private @Nullable Map<String, CustomEffectParameter> parameters;
	private @Nullable Double sessionCooldown;
	private @Nullable Double userCooldown;
	private @Nullable Boolean inactive;

	private CustomEffectBuilder(
		@NotNull CCName name,
		int price,
		@Nullable String description,
		@Nullable List<String> category,
		@Nullable String image,
		@Nullable CustomEffectDuration duration,
		@Nullable CustomEffectQuantity quantity,
		@Nullable Map<String, CustomEffectParameter> parameters,
		@Nullable Double sessionCooldown,
		@Nullable Double userCooldown,
		@Nullable Boolean inactive
	) {
		this.name = name;
		this.price = price;
		this.description = description;
		this.category = category;
		this.image = image;
		this.duration = duration;
		this.quantity = quantity;
		this.parameters = parameters;
		this.sessionCooldown = sessionCooldown;
		this.userCooldown = userCooldown;
		this.inactive = inactive;
	}

	/**
	 * Creates a staged builder which requires a {@code name} and then a {@code price}.
	 *
	 * @return name stage of the builder
	 */
	public static @NotNull NameStage builder() {
		return name -> price -> () -> new CustomEffectBuilder(name, price, null, null, null, null, null, null, null, null, null);
	}

	/**
	 * Creates a builder pre-populated with the values of an existing effect.
	 *
	 * @param from effect to copy
	 * @return builder
	 */
	public static @NotNull CustomEffectBuilder builder(@NotNull CustomEffect from) {
		return new CustomEffectBuilder(from.name(), from.price(), from.description(), from.category(), from.image(), from.duration(), from.quantity(), from.parameters(), from.sessionCooldown(), from.userCooldown(), from.inactive());
	}

	/**
	 * Equivalent to the {@link CustomEffect} constructor.
	 */
	public static @NotNull CustomEffect CustomEffect(
		@NotNull CCName name,
		int price,
		@Nullable String description,
		@Nullable List<String> category,
		@Nullable String image,
		@Nullable CustomEffectDuration duration,
		@Nullable CustomEffectQuantity quantity,
		@Nullable Map<String, CustomEffectParameter> parameters,
		@Nullable Double sessionCooldown,
		@Nullable Double userCooldown,
		@Nullable Boolean inactive
	) {
		return new CustomEffect(name, price, description, category, image, duration, quantity, parameters, sessionCooldown, userCooldown, inactive);
	}

	/**
	 * Wraps an existing effect to allow creating modified copies of it.
	 *
	 * @param from effect to wrap
	 * @return wither
	 */
	public static @NotNull With from(@NotNull CustomEffect from) {
		return new _FromWith(from);
	}

	/**
	 * Streams the components of an effect as name/value pairs.
	 *
	 * @param record effect
	 * @return stream of components
	 */
	public static @NotNull Stream<Map.Entry<String, Object>> stream(@NotNull CustomEffect record) {
		return Stream.of(
			new AbstractMap.SimpleImmutableEntry<>("name", record.name()),
			new AbstractMap.SimpleImmutableEntry<>("price", record.price()),
			new AbstractMap.SimpleImmutableEntry<>("description", record.description()),
			new AbstractMap.SimpleImmutableEntry<>("category", record.category()),
			new AbstractMap.SimpleImmutableEntry<>("image", record.image()),
			new AbstractMap.SimpleImmutableEntry<>("duration", record.duration()),
			new AbstractMap.SimpleImmutableEntry<>("quantity", record.quantity()),
			new AbstractMap.SimpleImmutableEntry<>("parameters", record.parameters()),
			new AbstractMap.SimpleImmutableEntry<>("sessionCooldown", record.sessionCooldown()),
			new AbstractMap.SimpleImmutableEntry<>("userCooldown", record.userCooldown()),
			new AbstractMap.SimpleImmutableEntry<>("inactive", record.inactive())
		);
	}

	public @NotNull CustomEffect build() {
		return new CustomEffect(name, price, description, category, image, duration, quantity, parameters, sessionCooldown, userCooldown, inactive);
	}

	public @NotNull CustomEffectBuilder name(@NotNull CCName name) {
		this.name = name;
		return this;
	}

	public @NotNull CCName name() {
		return name;
	}

	public @NotNull CustomEffectBuilder price(int price) {
		this.price = price;
		return this;
	}

	public int price() {
		return price;
	}

	public @NotNull CustomEffectBuilder description(@Nullable String description) {
		this.description = description;
		return this;
	}

	public @Nullable String description() {
		return description;
	}

	public @NotNull CustomEffectBuilder category(@Nullable List<String> category) {
		this.category = category;
		return this;
	}

	public @Nullable List<String> category() {
		return category;
	}

	public @NotNull CustomEffectBuilder image(@Nullable String image) {
		this.image = image;
		return this;
	}

	public @Nullable String image() {
		return image;
	}

	public @NotNull CustomEffectBuilder duration(@Nullable CustomEffectDuration duration) {
		this.duration = duration;
		return this;
	}

	public @Nullable CustomEffectDuration duration() {
		return duration;
	}

	public @NotNull CustomEffectBuilder quantity(@Nullable CustomEffectQuantity quantity) {
		this.quantity = quantity;
		return this;
	}

	public @Nullable CustomEffectQuantity quantity() {
		return quantity;
	}

	public @NotNull CustomEffectBuilder parameters(@Nullable Map<String, CustomEffectParameter> parameters) {
		this.parameters = parameters;
		return this;
	}

	public @Nullable Map<String, CustomEffectParameter> parameters() {
		return parameters;
	}

	public @NotNull CustomEffectBuilder sessionCooldown(@Nullable Double sessionCooldown) {
		this.sessionCooldown = sessionCooldown;
		return this;
	}

	public @Nullable Double sessionCooldown() {
		return sessionCooldown;
	}

	public @NotNull CustomEffectBuilder userCooldown(@Nullable Double userCooldown) {
		this.userCooldown = userCooldown;
		return this;
	}

	public @Nullable Double userCooldown() {
		return userCooldown;
	}

	public @NotNull CustomEffectBuilder inactive(@Nullable Boolean inactive) {
		this.inactive = inactive;
		return this;
	}

	public @Nullable Boolean inactive() {
		return inactive;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		CustomEffectBuilder that = (CustomEffectBuilder) obj;
		return Objects.equals(this.name, that.name) &&
			this.price == that.price &&
			Objects.equals(this.description, that.description) &&
			Objects.equals(this.category, that.category) &&
			Objects.equals(this.image, that.image) &&
			Objects.equals(this.duration, that.duration) &&
			Objects.equals(this.quantity, that.quantity) &&
			Objects.equals(this.parameters, that.parameters) &&
			Objects.equals(this.sessionCooldown, that.sessionCooldown) &&
			Objects.equals(this.userCooldown, that.userCooldown) &&
			Objects.equals(this.inactive, that.inactive);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, price, description, category, image, duration, quantity, parameters, sessionCooldown, userCooldown, inactive);
	}

	@Override
	public String toString() {
		return "CustomEffectBuilder[" +
			"name=" + name + ", " +
			"price=" + price + ", " +
			"description=" + description + ", " +
			"category=" + category + ", " +
			"image=" + image + ", " +
			"duration=" + duration + ", " +
			"quantity=" + quantity + ", " +
			"parameters=" + parameters + ", " +
			"sessionCooldown=" + sessionCooldown + ", " +
			"userCooldown=" + userCooldown + ", " +
			"inactive=" + inactive + ']';
	}

	/**
	 * First stage of the builder: requires the effect's name.
	 */
	@FunctionalInterface
	public interface NameStage {
		@NotNull PriceStage name(@NotNull CCName name);
	}

	/**
	 * Second stage of the builder: requires the effect's price.
	 */
	@FunctionalInterface
	public interface PriceStage {
		@NotNull CustomEffectBuilderStage price(int price);
	}

	/**
	 * Final stage of the builder: all required components are set,
	 * so optional components may be configured and the effect may be built.
	 */
	@FunctionalInterface
	public interface CustomEffectBuilderStage {
		@NotNull CustomEffectBuilder builder();

		default @NotNull CustomEffect build() {
			return builder().build();
		}

		default @NotNull CustomEffectBuilder description(@Nullable String description) {
			return builder().description(description);
		}

		default @NotNull CustomEffectBuilder category(@Nullable List<String> category) {
			return builder().category(category);
		}

		default @NotNull CustomEffectBuilder image(@Nullable String image) {
			return builder().image(image);
		}

		default @NotNull CustomEffectBuilder duration(@Nullable CustomEffectDuration duration) {
			return builder().duration(duration);
		}

		default @NotNull CustomEffectBuilder quantity(@Nullable CustomEffectQuantity quantity) {
			return builder().quantity(quantity);
		}

		default @NotNull CustomEffectBuilder parameters(@Nullable Map<String, CustomEffectParameter> parameters) {
			return builder().parameters(parameters);
		}

		default @NotNull CustomEffectBuilder sessionCooldown(@Nullable Double sessionCooldown) {
			return builder().sessionCooldown(sessionCooldown);
		}

		default @NotNull CustomEffectBuilder userCooldown(@Nullable Double userCooldown) {
			return builder().userCooldown(userCooldown);
		}

		default @NotNull CustomEffectBuilder inactive(@Nullable Boolean inactive) {
			return builder().inactive(inactive);
		}
	}

	/**
	 * Creates modified copies of a {@link CustomEffect}.
	 */
	public interface With {
		@NotNull CCName name();

		int price();

		@Nullable String description();

		@Nullable List<String> category();

		@Nullable String image();

		@Nullable CustomEffectDuration duration();

		@Nullable CustomEffectQuantity quantity();

		@Nullable Map<String, CustomEffectParameter> parameters();

		@Nullable Double sessionCooldown();

		@Nullable Double userCooldown();

		@Nullable Boolean inactive();

		default @NotNull CustomEffectBuilder with() {
			return new CustomEffectBuilder(name(), price(), description(), category(), image(), duration(), quantity(), parameters(), sessionCooldown(), userCooldown(), inactive());
		}

		default @NotNull CustomEffect with(@NotNull Consumer<CustomEffectBuilder> consumer) {
			CustomEffectBuilder builder = with();
			consumer.accept(builder);
			return builder.build();
		}

		default @NotNull CustomEffect withName(@NotNull CCName name) {
			return with().name(name).build();
		}

		default @NotNull CustomEffect withPrice(int price) {
			return with().price(price).build();
		}

		default @NotNull CustomEffect withDescription(@Nullable String description) {
			return with().description(description).build();
		}

		default @NotNull CustomEffect withCategory(@Nullable List<String> category) {
			return with().category(category).build();
		}

		default @NotNull CustomEffect withImage(@Nullable String image) {
			return with().image(image).build();
		}

		default @NotNull CustomEffect withDuration(@Nullable CustomEffectDuration duration) {
			return with().duration(duration).build();
		}

		default @NotNull CustomEffect withQuantity(@Nullable CustomEffectQuantity quantity) {
			return with().quantity(quantity).build();
		}

		default @NotNull CustomEffect withParameters(@Nullable Map<String, CustomEffectParameter> parameters) {
			return with().parameters(parameters).build();
		}

		default @NotNull CustomEffect withSessionCooldown(@Nullable Double sessionCooldown) {
			return with().sessionCooldown(sessionCooldown).build();
		}

		default @NotNull CustomEffect withUserCooldown(@Nullable Double userCooldown) {
			return with().userCooldown(userCooldown).build();
		}

		default @NotNull CustomEffect withInactive(@Nullable Boolean inactive) {
			return with().inactive(inactive).build();
		}
	}

	private static final class _FromWith implements With {
		private final @NotNull CustomEffect from;

		private _FromWith(@NotNull CustomEffect from) {
			this.from = from;
		}

		@Override
		public @NotNull CCName name() {
			return from.name();
		}

		@Override
		public int price() {
			return from.price();
		}

		@Override
		public @Nullable String description() {
			return from.description();
		}

		@Override
		public @Nullable List<String> category() {
			return from.category();
		}

		@Override
		public @Nullable String image() {
			return from.image();
		}

		@Override
		public @Nullable CustomEffectDuration duration() {
			return from.duration();
		}

		@Override
		public @Nullable CustomEffectQuantity quantity() {
			return from.quantity();
		}

		@Override
		public @Nullable Map<String, CustomEffectParameter> parameters() {
			return from.parameters();
		}

		@Override
		public @Nullable Double sessionCooldown() {
			return from.sessionCooldown();
		}

		@Override
		public @Nullable Double userCooldown() {
			return from.userCooldown();
		}

		@Override
		public @Nullable Boolean inactive() {
			return from.inactive();
		}
	}
}
