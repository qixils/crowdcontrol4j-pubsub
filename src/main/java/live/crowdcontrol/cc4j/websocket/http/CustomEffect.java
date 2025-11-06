package live.crowdcontrol.cc4j.websocket.http;

import io.soabase.recordbuilder.core.RecordBuilder;
import live.crowdcontrol.cc4j.websocket.payload.CCName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@RecordBuilder
@RecordBuilder.Options(
	builderMode = RecordBuilder.BuilderMode.STAGED_REQUIRED_ONLY,
	skipStagingForInitializedComponents = true,
	nullableAnnotationClass = "org.jetbrains.annotations.Nullable",
	allowNullableCollections = true
)
public record CustomEffect(
	@NotNull CCName name,
	int price,
	@RecordBuilder.Initializer("DEFAULT_DESCRIPTION") @Nullable String description,
	@RecordBuilder.Initializer("DEFAULT_CATEGORY") @Nullable List<String> category,
	@RecordBuilder.Initializer("DEFAULT_IMAGE") @Nullable String image,
	@RecordBuilder.Initializer("DEFAULT_DURATION") @Nullable CustomEffectDuration duration,
	@RecordBuilder.Initializer("DEFAULT_QUANTITY") @Nullable CustomEffectQuantity quantity,
	@RecordBuilder.Initializer("DEFAULT_PARAMETERS") @Nullable Map<String, CustomEffectParameter> parameters,
	@RecordBuilder.Initializer("DEFAULT_SESSIONCOOLDOWN") @Nullable Double sessionCooldown,
	@RecordBuilder.Initializer("DEFAULT_USERCOOLDOWN") @Nullable Double userCooldown,
	@RecordBuilder.Initializer("DEFAULT_INACTIVE") @Nullable Boolean inactive
) {
	// this is so awesome and so cool but whatever
	public static final @Nullable String DEFAULT_DESCRIPTION = null;
	public static final @Nullable List<String> DEFAULT_CATEGORY = null;
	public static final @Nullable String DEFAULT_IMAGE = null;
	public static final @Nullable CustomEffectDuration DEFAULT_DURATION = null;
	public static final @Nullable CustomEffectQuantity DEFAULT_QUANTITY = null;
	public static final @Nullable Map<String, CustomEffectParameter> DEFAULT_PARAMETERS = null;
	public static final @Nullable Double DEFAULT_SESSIONCOOLDOWN = null;
	public static final @Nullable Double DEFAULT_USERCOOLDOWN = null;
	public static final @Nullable Boolean DEFAULT_INACTIVE = null;
}
