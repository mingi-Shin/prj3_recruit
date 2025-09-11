document.addEventListener('DOMContentLoaded', function() {

	//모달창 열기
	document.getElementById('position-btn').addEventListener('click', function() {
		document.getElementById('interviewModal').style.display = 'block';
	});

	//모달창 닫기
	document.getElementById('modal-close').addEventListener('click', function() {
		document.getElementById('interviewModal').style.display = 'none';
	});

	//목록으로 이동
	document.getElementById('list-btn').addEventListener('click', function() {
		window.history.back();
	});

	//북마크 처리
	document.getElementById('bookmark-btn').addEventListener('click', async function() {
		const btn = document.querySelector('.bookmark-btn');
		const isBookMarked = btn.classList.contains('active');

		try {
			if (isBookMarked) {
				await removeBookmark();
			} else {
				await addBookmark();
			}
		} catch (error) {
			console.error('북마크 처리 실패:', error);
		}
	});

	//메일 보내기 클릭시
	document.querySelector('#interviewModal form').addEventListener('submit', function() {
		alert('메일이 전송되었습니다.');
	});

	//이력서 다운로드
	document.getElementById('download-btn').addEventListener('click', function() {
		const resumeSeq = this.dataset.resumeSeq;
		location.href = '/corp/applicant/download/' + resumeSeq;
	});

	//첨부파일 다운로드
	document.getElementById('fileListContainer').addEventListener('click', function(e) {
		const fileItem = e.target.closest('.sidebar-attachment-item');
		const attachmentSeq = fileItem.getAttribute('data-value');

		const downloadLink = document.createElement('a');
		downloadLink.href = '/corp/attachment/download/' + attachmentSeq;
		downloadLink.download = ''; 
		document.body.appendChild(downloadLink);
		downloadLink.click();
		document.body.removeChild(downloadLink);
		return;

	});


});//DOMContentLoaded

//북마크 생성
async function addBookmark() {
	const resumeSeq = document.getElementById('bookmark-btn').dataset.resumeSeq;
	const btn = document.querySelector('.bookmark-btn');

	const response = await fetch('/corp/bookmark/add/' + resumeSeq, {
		method: 'POST'
	})
	if (!response.ok) {
		throw new Error("북마크 추가 실패")
	}
	const result = await response.text();
	if (result == 'success') btn.classList.add('active');

}

//북마크 제거
async function removeBookmark() {
	const resumeSeq = document.getElementById('bookmark-btn').dataset.resumeSeq;
	const btn = document.querySelector('.bookmark-btn');

	const response = await fetch('/corp/bookmark/remove/' + resumeSeq, {
		method: 'POST'
	})
	if (!response.ok) {
		throw new Error("북마크 제거 실패")
	}
	const result = await response.text();
	if (result == 'success') btn.classList.remove('active');
}

