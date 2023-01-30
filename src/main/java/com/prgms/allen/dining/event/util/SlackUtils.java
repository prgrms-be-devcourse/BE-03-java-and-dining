package com.prgms.allen.dining.event.util;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.event.dto.SlackMessageRes;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.model.ModelConfigurator;
import com.slack.api.model.block.HeaderBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.composition.TextObject;

@Component
public class SlackUtils {

	public static final String CUSTOMER_NAME_PREFIX = "*예약자 명:*\n";
	public static final String CUSTOMER_PHONE_PREFIX = "*예약자 전화번호:*\n";
	public static final String VISITOR_COUNT_PREFIX = "*예약자 인원수:*\n";
	public static final String VISIT_DATE_TIME_PREFIX = "*예약 날짜:*\n";
	private static final Logger log = LoggerFactory.getLogger(SlackUtils.class);
	private static final String customerChannel = "#03-allen-customer-java-and-dining";
	private static final String ownerChannel = "#03-allen-owner-java-and-dining";

	private static String token;

	public static void notify(SlackMessageRes slackMessageRes, MemberType memberType) {

		final String channel = getChannelBy(memberType);

		final ChatPostMessageRequest message = createMessage(slackMessageRes, channel);

		try {
			Slack.getInstance()
				.methods(token)
				.chatPostMessage(message);
		} catch (IOException | SlackApiException e) {
			log.warn("Can't send Slack Message");
		}
	}

	private static String getChannelBy(MemberType memberType) {
		return switch (memberType) {
			case OWNER -> ownerChannel;
			case CUSTOMER -> customerChannel;
		};
	}

	private static ChatPostMessageRequest createMessage(
		SlackMessageRes slackMessageRes,
		String channel
	) {
		final List<TextObject> messageBody = createMessageBody(slackMessageRes);

		return ChatPostMessageRequest.builder()
			.channel(channel)
			.blocks(createMessageForm(slackMessageRes, messageBody))
			.build();
	}

	private static List<LayoutBlock> createMessageForm(SlackMessageRes slackMessageRes, List<TextObject> messageBody) {
		return asBlocks(
			header(createMessageHeader(slackMessageRes)),
			divider(),
			section(section -> section.fields(messageBody)));
	}

	private static ModelConfigurator<HeaderBlock.HeaderBlockBuilder> createMessageHeader(
		SlackMessageRes slackMessageRes
	) {
		return header -> header.text(plainText(
			MessageFormat.format(
				"[{0}] {1}",
				slackMessageRes.restaurantName(),
				slackMessageRes.headerMessage().getMessage())
		));
	}

	private static List<TextObject> createMessageBody(SlackMessageRes slackMessageRes) {
		return List.of(
			markdownText(CUSTOMER_NAME_PREFIX + slackMessageRes.customerName()),
			markdownText(CUSTOMER_PHONE_PREFIX + slackMessageRes.customerPhone()),
			markdownText(VISITOR_COUNT_PREFIX + slackMessageRes.visitorCount()),
			markdownText(VISIT_DATE_TIME_PREFIX + slackMessageRes.visitDateTime())
		);
	}

	@Value(value = "${slack.token}")
	public void setToken(String value) {
		token = value;
	}
}
