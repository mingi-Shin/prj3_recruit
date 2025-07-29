document.addEventListener('DOMContentLoaded', function() {

	const pagination = document.querySelector('.pagination-container');

	//페이지 버튼 클릭시
	document.querySelectorAll('.page-btn[data-page]').forEach(button => {
		button.addEventListener('click', function() {
			const clickPage = this.getAttribute('data-page');
			movePage(clickPage);
		});
	});

	//이전 버튼 클릭시
	document.querySelector('.prev').addEventListener('click', function() {
		const startPage = parseInt(pagination.dataset.startPage);
		const pageGroup = parseInt(pagination.dataset.pageGroup);
		const prevPage = startPage - pageGroup;
		if (prevPage >= 1) {
			movePage(prevPage)
		}
	});

	//이후 버튼 클릭시
	document.querySelector('.next').addEventListener('click', function() {
		const endPage = parseInt(pagination.dataset.endPage);
		const totalPage = parseInt(pagination.dataset.totalPage);
		const nextPage = endPage + 1;
		if (nextPage <= totalPage) {
			movePage(nextPage)
		}
	});

	//각 필터 버튼 클릭시
	document.querySelectorAll('.stats-item').forEach(item => {
		item.addEventListener('click', function() {
			const type = item.dataset.filter;
			movePage(1, type);
		});
	});

	//검색 버튼 및 enter 버튼 클릭시
	document.getElementById('search').addEventListener('click', function() {
		movePage(1);
	});

	document.getElementById('keyword').addEventListener('keydown', function(e) {
		if (e.key === 'Enter') {
			e.preventDefault();
			movePage(1);
		}
	});

	//메일 클릭시 메일 상세내용 뜨기
	document.querySelectorAll('.application-item').forEach(function(item) {
		item.addEventListener('click', async function() {
			const title = item.querySelector('.job-title')?.textContent || '제목 없음';
			const company = item.querySelector('.company-name')?.textContent || '회사명';
			const date = item.querySelector('.application-meta')?.textContent || '날짜 정보 없음';
			const content = item.getAttribute('data-content') || '내용 없음';

			document.getElementById('modalTitle').textContent = title;
			document.getElementById('modalCompany').textContent = company;
			document.getElementById('modalDate').textContent = date;
			document.getElementById('modalMessage').innerHTML = content;

			document.getElementById('mailModal').style.display = 'flex';


			//이미 읽은 메일이면 읽음 처리 필요없으니 return
			if (item.classList.contains('read-mail')) return;

			//처음 클릭한 메일이면 메일 읽음 처리
			const messageSeq = item.getAttribute('data-messageSeq');

			try {
				const response = await fetch('/mypage/message/' + messageSeq, {
					method: "PUT"
				});

				if (response.ok) {
					item.classList.add('read-mail');
					updateCounts(1, 'toRead')
				} else {
					alert("서버 응답 오류")
				}

			} catch (error) {
				console.error("에러 발생:", error);
			}
		});
	});

	//모달 닫기
	document.querySelector('.modal-close').addEventListener('click', function() {
		document.getElementById('mailModal').style.display = 'none';
	});

	//모달 밖 클릭시 닫기
	document.querySelector('.modal-overlay').addEventListener('click', function() {
		document.getElementById('mailModal').style.display = 'none';
	});

	// 체크박스 클릭시 모달 안 뜨도록 막기
	document.querySelectorAll('.application-item input[type="checkbox"]').forEach(checkbox => {
		checkbox.addEventListener('click', function(event) {
			event.stopPropagation();
		});
	});

	// 전체 체크박스 클릭
	document.getElementById('selectAllCheckbox').addEventListener('change', function() {
		const checked = this.checked;
		document.querySelectorAll('.mail-item-checkbox').forEach(chk => {
			chk.checked = checked;
		});
	});


	//읽음 처리 버튼 클릭
	document.getElementById('markReadBtn').addEventListener('click', async function() {
		let selectedCheckboxes = getCheckedCheckboxes();
		let selectedSeq = [];

		// 선택된 체크박스들 중에서 안읽은 메일들만 선별
		selectedCheckboxes.forEach(checkbox => {
			const mailItem = checkbox.closest('.application-item');
			if (!mailItem.classList.contains('read-mail')) {
				selectedSeq.push(checkbox.value);
			}
		});

		if (selectedSeq.length == 0) return;

		try {
			const response = await fetch('/mypage/messages/' + selectedSeq, {
				method: "PUT"
			});
			if (response.ok) {
				selectedCheckboxes.forEach(checkbox => {
					const mailItem = checkbox.closest('.application-item');
					if (!mailItem.classList.contains('read-mail')) {
						mailItem.classList.add('read-mail');
					}
				});
				
				updateCounts(selectedSeq.length, 'toRead')
			} else {
				alert("읽음 처리에 실패했습니다.");
			}
		} catch {
			console.error("에러 발생:", error);
			alert("서버 오류가 발생했습니다.");
		}

	});

	//안읽음 처리 버튼 클릭
	document.getElementById('markUnreadBtn').addEventListener('click', async function() {
		let selectedCheckboxes = getCheckedCheckboxes();
		let selectedSeq = [];

		//읽은 메일들만 선별
		selectedCheckboxes.forEach(checkbox => {
			const mailItem = checkbox.closest('.application-item');
			if (mailItem.classList.contains('read-mail')) {
				selectedSeq.push(checkbox.value);
			}
		});

		if (selectedSeq.length == 0) return;

		try {
			const response = await fetch('/mypage/messages/' + selectedSeq, {
				method: "PUT"
			});
			if (response.ok) {
				selectedCheckboxes.forEach(checkbox => {
					const mailItem = checkbox.closest('.application-item');
					if (mailItem.classList.contains('read-mail')) {
						mailItem.classList.remove('read-mail');
					}
				});
				
				updateCounts(selectedSeq.length, 'unRead')
			} else {
				alert("안읽음 처리에 실패했습니다.");
			}
		} catch {
			console.error("에러 발생:", error);
			alert("서버 오류가 발생했습니다.");
		}
	});

	//삭제 버튼 클릭
	document.getElementById('deleteBtn').addEventListener('click', async function() {
		let selectedSeq = getCheckedCheckboxes().map(checkbox => checkbox.value);

		if (selectedSeq.length == 0) {
			alert("삭제할 메일을 선택해주세요.")
			return;
		}

		if (!confirm('선택한 메일을 정말 삭제하시겠습니까?')) return;

		try {
			const response = await fetch('/mypage/messages/' + selectedSeq, {
				method: 'DELETE'
			});

			if (response.ok) {
				location.reload();
			}
			else {
				alert("삭제 실패")
			}
		} catch (error) {
			console.error("에러 발생:", error);
			alert("서버 오류 발생");
		}
	});

});

//페이지 이동
function movePage(currentPage, type) {
	const keyword = document.querySelector('input[name="keyword"]').value
	const params = new URLSearchParams(location.search);
	params.set("page", currentPage);

	if (keyword !== "") {
		params.set("keyword", keyword);
	}
	
	// type을 새로 전달했다면 반영, 아니면 기존 유지
	if (type !== undefined && type !== "") {
		params.set("type", type);
	}

	location.href = "/user/mypage/mail_list?" + params.toString();
}

// 체크된 체크박스들을 반환하는 함수 (기존 getCheckedValue 함수 수정)
function getCheckedCheckboxes() {
	let selectedCheckboxes = [];
	document.querySelectorAll('.mail-item-checkbox:checked').forEach(checkbox => {
		selectedCheckboxes.push(checkbox);
	});
	return selectedCheckboxes;
}

function updateCounts(cnt, type) {
	const unread = document.getElementById('unread-count');
	const read = document.getElementById('read-count');

	// 문자열 → 숫자로 변환
	let unreadCnt = parseInt(unread.textContent);
	let readCnt = parseInt(read.textContent);

	if (type === 'toRead') {
		unreadCnt -= cnt;
		readCnt += cnt;
	} else if (type === 'unRead') {
		readCnt -= cnt;
		unreadCnt += cnt;
	}

	unread.textContent = unreadCnt;
	read.textContent = readCnt;
}



//ajax로 메일 목록 갱신하기
/*async function movePage(currentPage) {
	const params = new URLSearchParams({
		page: currentPage,
	});

	const response = await fetch('/mypage/messages?' + params.toString(), {
		method: 'GET',
		headers: { 'Content-Type': 'application/json' },
	});

	if (!response.ok) {
		throw new Error("메일을 불러오는데 실패했습니다.")
	}

	const result = await response.json();
	if (result.result == 'success') {
		const listContainer = document.querySelector('.application-list');
		listContainer.innerHTML = '';

		result.data.forEach(message => {
			const div = document.createElement('div');
			div.className = 'application-item';
			if (message.isRead === 'Y') {
				div.classList.add('read-mail');
			}
			div.dataset.messageSeq = message.messageSeq;
			div.dataset.content = message.messageContent;

			div.innerHTML = `
				<div calss="application-info">
				<div class="company-name">${message.corpNm}</div>
				<div class="job-title">${message.messageTitle}</div>
				<div class="application-meta">${message.createdAt}</div>
				</div>
			`;
			
			listContainer.appendChild(div);
		});
	}

}*/
