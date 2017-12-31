package co.hooghly.commerce.business;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.hooghly.commerce.domain.Order;
import co.hooghly.commerce.domain.Transaction;
import co.hooghly.commerce.domain.TransactionType;
import co.hooghly.commerce.repository.TransactionRepository;



@Service
public class TransactionService  extends SalesManagerEntityServiceImpl<Long, Transaction>  {
	

	private TransactionRepository transactionRepository;
	
	
	public TransactionService(TransactionRepository transactionRepository) {
		super(transactionRepository);
		this.transactionRepository = transactionRepository;
	}
	
	
	public void create(Transaction transaction) throws ServiceException {
		
		//parse JSON string
		String transactionDetails = transaction.toJSONString();
		if(!StringUtils.isBlank(transactionDetails)) {
			transaction.setDetails(transactionDetails);
		}
		
		super.create(transaction);
		
		
	}
	
	
	public List<Transaction> listTransactions(Order order) throws ServiceException {
		
		List<Transaction> transactions = transactionRepository.findByOrder(order.getId());
		ObjectMapper mapper = new ObjectMapper();
		for(Transaction transaction : transactions) {
				if(!StringUtils.isBlank(transaction.getDetails())) {
					try {
						@SuppressWarnings("unchecked")
						Map<String,String> objects = mapper.readValue(transaction.getDetails(), Map.class);
						transaction.setTransactionDetails(objects);
					} catch (Exception e) {
						throw new ServiceException(e);
					}
				}
		}
		
		return transactions;
	}

	
	public Transaction getCapturableTransaction(Order order)
			throws ServiceException {
		List<Transaction> transactions = transactionRepository.findByOrder(order.getId());
		ObjectMapper mapper = new ObjectMapper();
		Transaction capturable = null;
		for(Transaction transaction : transactions) {
			if(transaction.getTransactionType().name().equals(TransactionType.AUTHORIZE.name())) {
				if(!StringUtils.isBlank(transaction.getDetails())) {
					try {
						@SuppressWarnings("unchecked")
						Map<String,String> objects = mapper.readValue(transaction.getDetails(), Map.class);
						transaction.setTransactionDetails(objects);
						capturable = transaction;
					} catch (Exception e) {
						throw new ServiceException(e);
					}
				}
			}
			if(transaction.getTransactionType().name().equals(TransactionType.CAPTURE.name())) {
				break;
			}
			if(transaction.getTransactionType().name().equals(TransactionType.REFUND.name())) {
				break;
			}
		}
		
		return capturable;
	}
	
	
	public Transaction getRefundableTransaction(Order order)
		throws ServiceException {
		List<Transaction> transactions = transactionRepository.findByOrder(order.getId());
		Map<String,Transaction> finalTransactions = new HashMap<String,Transaction>();
		Transaction finalTransaction = null;
		for(Transaction transaction : transactions) {
			//System.out.println("Transaction type " + transaction.getTransactionType().name());
			if(transaction.getTransactionType().name().equals(TransactionType.AUTHORIZECAPTURE.name())) {
				finalTransactions.put(TransactionType.AUTHORIZECAPTURE.name(),transaction);
				continue;
			}
			if(transaction.getTransactionType().name().equals(TransactionType.CAPTURE.name())) {
				finalTransactions.put(TransactionType.CAPTURE.name(),transaction);
				continue;
			}
			if(transaction.getTransactionType().name().equals(TransactionType.REFUND.name())) {
				//check transaction id
				Transaction previousRefund = finalTransactions.get(TransactionType.REFUND.name());
				if(previousRefund!=null) {
					Date previousDate = previousRefund.getTransactionDate();
					Date currentDate = transaction.getTransactionDate();
					if(previousDate.before(currentDate)) {
						finalTransactions.put(TransactionType.REFUND.name(),transaction);
						continue;
					}
				} else {
					finalTransactions.put(TransactionType.REFUND.name(),transaction);
					continue;
				}
			}
		}
		
		if(finalTransactions.containsKey(TransactionType.AUTHORIZECAPTURE.name())) {
			finalTransaction = finalTransactions.get(TransactionType.AUTHORIZECAPTURE.name());
		}
		
		if(finalTransactions.containsKey(TransactionType.CAPTURE.name())) {
			finalTransaction = finalTransactions.get(TransactionType.CAPTURE.name());
		}
		
		//if(finalTransactions.containsKey(TransactionType.REFUND.name())) {
		//	finalTransaction = finalTransactions.get(TransactionType.REFUND.name());
		//}

		
		if(finalTransaction!=null && !StringUtils.isBlank(finalTransaction.getDetails())) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				@SuppressWarnings("unchecked")
				Map<String,String> objects = mapper.readValue(finalTransaction.getDetails(), Map.class);
				finalTransaction.setTransactionDetails(objects);
			} catch (Exception e) {
				throw new ServiceException(e);
			}
		}
		
		return finalTransaction;
	}

}
