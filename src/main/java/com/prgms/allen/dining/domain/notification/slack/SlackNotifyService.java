package com.prgms.allen.dining.domain.notification.slack;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.notification.slack.dto.HeaderMessage;
import com.prgms.allen.dining.domain.notification.slack.dto.SlackNotificationMessageRes;
import com.prgms.allen.dining.domain.reservation.entity.Reservation;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;
import com.prgms.allen.dining.global.error.exception.NotificationFailedException;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.model.ModelConfigurator;
import com.slack.api.model.block.HeaderBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.composition.TextObject;

@Service
public class SlackNotifyService {

	private static final String CUSTOMER_NAME_PREFIX = "*예약자 명:*\n";
	private static final String CUSTOMER_PHONE_PREFIX = "*예약자 전화번호:*\n";
	private static final String VISITOR_COUNT_PREFIX = "*예약자 인원수:*\n";
	private static final String VISIT_DATE_TIME_PREFIX = "*예약 날짜:*\n";
	private static final String customerChannel = "#03-allen-customer-java-and-dining";
	private static final String ownerChannel = "#03-allen-owner-java-and-dining";
	private static String token;

	@Value(value = "${slack.token}")
	public void setToken(String value) {
		token = value;
	}

	public void notifyReserve(Reservation reservation) {
		if (checkVisitingToday(reservation.getCustomerInput())) {
			notifyAll(reservation, HeaderMessage.RESERVATION_CONFIRMED);
			return;
		}

		notifyAll(reservation, HeaderMessage.RESERVATION_ACCEPTED);
	}

	private boolean checkVisitingToday(ReservationCustomerInput customerInput) {
		return customerInput.checkVisitingToday();
	}

	public void notifyConfirm(Reservation reservation) {
		notifyAll(reservation, HeaderMessage.RESERVATION_CONFIRMED);
	}

	public void notifyCancel(Reservation reservation) {
		notifyAll(reservation, HeaderMessage.RESERVATION_CANCELED);
	}

	private void notifyAll(Reservation reservation, HeaderMessage headerMessage) {
		notify(
			new SlackNotificationMessageRes(reservation, headerMessage),
			MemberType.OWNER
		);
		notify(
			new SlackNotificationMessageRes(reservation, headerMessage),
			MemberType.CUSTOMER
		);
	}

	private void notify(SlackNotificationMessageRes slackNotificationMessageRes, MemberType memberType) {

		final String channel = getChannelBy(memberType);

		final ChatPostMessageRequest message = createMessage(slackNotificationMessageRes, channel);

		try {
			Slack.getInstance()
				.methods(token)
				.chatPostMessage(message);
		} catch (IOException | SlackApiException e) {
			throw new NotificationFailedException(
				MessageFormat.format("Failed to send slack message. [token]: {0}", token)
			);
		}
	}

	private String getChannelBy(MemberType memberType) {
		return switch (memberType) {
			case OWNER -> ownerChannel;
			case CUSTOMER -> customerChannel;
		};
	}

	private ChatPostMessageRequest createMessage(
		SlackNotificationMessageRes slackNotificationMessageRes,
		String channel
	) {
		final List<TextObject> messageBody = createMessageBody(slackNotificationMessageRes);

		return ChatPostMessageRequest.builder()
			.channel(channel)
			.blocks(createMessageForm(slackNotificationMessageRes, messageBody))
			.build();
	}

	private List<LayoutBlock> createMessageForm(SlackNotificationMessageRes slackNotificationMessageRes,
		List<TextObject> messageBody) {
		return asBlocks(
			header(createMessageHeader(slackNotificationMessageRes)),
			divider(),
			section(section -> section.fields(messageBody)));
	}

	private ModelConfigurator<HeaderBlock.HeaderBlockBuilder> createMessageHeader(
		SlackNotificationMessageRes slackNotificationMessageRes
	) {
		return header -> header.text(plainText(
			MessageFormat.format(
				"[{0}] {1}",
				slackNotificationMessageRes.restaurantName(),
				slackNotificationMessageRes.headerMessage().getMessage())
		));
	}

	private List<TextObject> createMessageBody(SlackNotificationMessageRes slackNotificationMessageRes) {
		return List.of(
			markdownText(CUSTOMER_NAME_PREFIX + slackNotificationMessageRes.customerName()),
			markdownText(CUSTOMER_PHONE_PREFIX + slackNotificationMessageRes.customerPhone()),
			markdownText(VISITOR_COUNT_PREFIX + slackNotificationMessageRes.visitorCount()),
			markdownText(VISIT_DATE_TIME_PREFIX + slackNotificationMessageRes.visitDateTime())
		);
	}
}
